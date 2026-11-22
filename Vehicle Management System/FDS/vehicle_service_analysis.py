"""
Vehicle Service Management and Maintenance Analysis System
===========================================================
Data science / visualization module that connects to the MySQL
VehicleServiceDB database, retrieves data from all tables, performs
preprocessing, prints summary statistics, and generates 8 professional
charts saved as PNG files in the output/ directory.

Requirements:
    pip install -r requirements.txt

Usage:
    python vehicle_service_analysis.py
"""

import os
import sys
import warnings

import numpy as np
import pandas as pd
import matplotlib
matplotlib.use("Agg")                       # non-interactive backend
import matplotlib.pyplot as plt
import seaborn as sns
import mysql.connector

# ──────────────────────────────────────────────
# Configuration
# ──────────────────────────────────────────────
DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "database": "VehicleServiceDB",
    "user": "root",
    "password": "root",
}

OUTPUT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "output")
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Suppress minor warnings for clean output
warnings.filterwarnings("ignore", category=UserWarning)

# ──────────────────────────────────────────────
# Seaborn / Matplotlib global style
# ──────────────────────────────────────────────
sns.set_theme(style="whitegrid", palette="Set2", font_scale=1.1)
plt.rcParams.update({
    "figure.dpi": 150,
    "savefig.bbox": "tight",
    "axes.titleweight": "bold",
})

# ──────────────────────────────────────────────
# Database helpers
# ──────────────────────────────────────────────

def get_connection():
    """Return a MySQL connection using the project DB config."""
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        return conn
    except mysql.connector.Error as err:
        print(f"[ERROR] Could not connect to MySQL: {err}")
        sys.exit(1)


def fetch_table(conn, table_name: str) -> pd.DataFrame:
    """Read an entire table into a DataFrame."""
    query = f"SELECT * FROM {table_name}"
    return pd.read_sql(query, conn)


def load_all_data(conn):
    """Fetch every project table and return a dict of DataFrames."""
    tables = {}
    for name in ("Customer", "Vehicle", "Appointment", "Service_Record", "Bill"):
        tables[name] = fetch_table(conn, name)
        print(f"  ✓ Loaded {name:20s} → {len(tables[name]):>4d} rows")
    return tables


# ──────────────────────────────────────────────
# Data preprocessing
# ──────────────────────────────────────────────

def preprocess(tables: dict) -> dict:
    """Clean and type-convert all DataFrames."""
    # --- Customer ---
    cust = tables["Customer"].copy()
    cust["customer_name"] = cust["customer_name"].fillna("Unknown")
    cust["phone"] = cust["phone"].fillna("N/A")
    cust["email"] = cust["email"].fillna("N/A")
    cust["address"] = cust["address"].fillna("N/A")
    if "created_at" in cust.columns:
        cust["created_at"] = pd.to_datetime(cust["created_at"], errors="coerce")
    tables["Customer"] = cust

    # --- Vehicle ---
    veh = tables["Vehicle"].copy()
    veh["brand"] = veh["brand"].fillna("Unknown")
    veh["model"] = veh["model"].fillna("Unknown")
    veh["vehicle_type"] = veh["vehicle_type"].fillna("Unknown")
    veh["manufacture_year"] = pd.to_numeric(veh["manufacture_year"], errors="coerce").fillna(0).astype(int)
    tables["Vehicle"] = veh

    # --- Appointment ---
    appt = tables["Appointment"].copy()
    appt["appointment_date"] = pd.to_datetime(appt["appointment_date"], errors="coerce")
    appt["status"] = appt["status"].fillna("BOOKED")
    appt["issue_description"] = appt["issue_description"].fillna("")
    tables["Appointment"] = appt

    # --- Service_Record ---
    srv = tables["Service_Record"].copy()
    srv["service_date"] = pd.to_datetime(srv["service_date"], errors="coerce")
    for col in ("labor_cost", "parts_cost", "total_cost"):
        srv[col] = pd.to_numeric(srv[col], errors="coerce").fillna(0)
    srv["service_description"] = srv["service_description"].fillna("General Service")
    if "next_service_date" in srv.columns:
        srv["next_service_date"] = pd.to_datetime(srv["next_service_date"], errors="coerce")
    tables["Service_Record"] = srv

    # --- Bill ---
    bill = tables["Bill"].copy()
    bill["bill_date"] = pd.to_datetime(bill["bill_date"], errors="coerce")
    bill["amount"] = pd.to_numeric(bill["amount"], errors="coerce").fillna(0)
    bill["payment_status"] = bill["payment_status"].fillna("PENDING")
    bill["payment_method"] = bill["payment_method"].fillna("N/A")
    tables["Bill"] = bill

    return tables


# ──────────────────────────────────────────────
# Summary statistics
# ──────────────────────────────────────────────

def print_summary(tables: dict):
    """Print key summary statistics to the console."""
    srv = tables["Service_Record"]
    bill = tables["Bill"]
    veh = tables["Vehicle"]
    cust = tables["Customer"]

    print("\n" + "=" * 60)
    print("          SUMMARY STATISTICS")
    print("=" * 60)
    print(f"  Total Customers            : {len(cust)}")
    print(f"  Total Vehicles             : {len(veh)}")
    print(f"  Total Service Records      : {len(srv)}")
    print(f"  Total Bills                : {len(bill)}")
    print("-" * 60)
    print(f"  Total Revenue (Bills)      : ₹{bill['amount'].sum():,.2f}")
    print(f"  Average Service Cost       : ₹{srv['total_cost'].mean():,.2f}")
    print(f"  Max Service Cost           : ₹{srv['total_cost'].max():,.2f}")
    print(f"  Min Service Cost           : ₹{srv['total_cost'].min():,.2f}")
    print(f"  Median Service Cost        : ₹{srv['total_cost'].median():,.2f}")
    print("-" * 60)
    print(f"  Average Labor Cost         : ₹{srv['labor_cost'].mean():,.2f}")
    print(f"  Average Parts Cost         : ₹{srv['parts_cost'].mean():,.2f}")
    print("-" * 60)
    paid = bill[bill["payment_status"].str.upper() == "PAID"]
    pending = bill[bill["payment_status"].str.upper() == "PENDING"]
    print(f"  Bills PAID                 : {len(paid)}  (₹{paid['amount'].sum():,.2f})")
    print(f"  Bills PENDING              : {len(pending)}  (₹{pending['amount'].sum():,.2f})")
    print("=" * 60 + "\n")


# ──────────────────────────────────────────────
# Chart generators
# ──────────────────────────────────────────────

def _save(fig, filename: str):
    path = os.path.join(OUTPUT_DIR, filename)
    fig.savefig(path)
    plt.close(fig)
    print(f"  ✓ Saved {filename}")


def chart_maintenance_cost_by_vehicle(tables: dict):
    """1. Horizontal bar chart – Maintenance Cost by Vehicle."""
    srv = tables["Service_Record"]
    veh = tables["Vehicle"]
    merged = srv.merge(veh, on="vehicle_id", how="left")
    cost_by_veh = (
        merged.groupby("vehicle_number")["total_cost"]
        .sum()
        .sort_values()
    )

    fig, ax = plt.subplots(figsize=(10, 6))
    colors = sns.color_palette("viridis", len(cost_by_veh))
    cost_by_veh.plot.barh(ax=ax, color=colors, edgecolor="black", linewidth=0.5)
    ax.set_xlabel("Total Maintenance Cost (₹)")
    ax.set_ylabel("Vehicle Number")
    ax.set_title("Maintenance Cost by Vehicle")
    for i, (val, name) in enumerate(zip(cost_by_veh.values, cost_by_veh.index)):
        ax.text(val + cost_by_veh.max() * 0.01, i, f"₹{val:,.0f}", va="center", fontsize=9)
    _save(fig, "01_maintenance_cost_by_vehicle.png")


def chart_services_by_month(tables: dict):
    """2. Bar chart – Number of Services by Month."""
    srv = tables["Service_Record"].copy()
    srv["month"] = srv["service_date"].dt.to_period("M").astype(str)
    month_counts = srv.groupby("month").size().reset_index(name="count")

    fig, ax = plt.subplots(figsize=(10, 6))
    sns.barplot(data=month_counts, x="month", y="count", hue="month", palette="coolwarm", ax=ax, edgecolor="black", legend=False)
    ax.set_xlabel("Month")
    ax.set_ylabel("Number of Services")
    ax.set_title("Number of Services by Month")
    plt.xticks(rotation=45, ha="right")
    for p in ax.patches:
        ax.annotate(f"{int(p.get_height())}", (p.get_x() + p.get_width() / 2, p.get_height()),
                     ha="center", va="bottom", fontsize=10, fontweight="bold")
    _save(fig, "02_services_by_month.png")


def chart_service_type_distribution(tables: dict):
    """3. Pie chart – Service Type Distribution."""
    srv = tables["Service_Record"]
    type_counts = srv["service_description"].value_counts()

    fig, ax = plt.subplots(figsize=(8, 8))
    wedges, texts, autotexts = ax.pie(
        type_counts.values,
        labels=type_counts.index,
        autopct="%1.1f%%",
        startangle=140,
        colors=sns.color_palette("pastel", len(type_counts)),
        pctdistance=0.85,
        wedgeprops={"edgecolor": "black", "linewidth": 0.5},
    )
    for t in autotexts:
        t.set_fontsize(9)
        t.set_fontweight("bold")
    ax.set_title("Service Type Distribution")
    _save(fig, "03_service_type_distribution.png")


def chart_monthly_revenue_trend(tables: dict):
    """4. Line chart – Monthly Revenue Trend."""
    bill = tables["Bill"].copy()
    bill["month"] = bill["bill_date"].dt.to_period("M").astype(str)
    revenue = bill.groupby("month")["amount"].sum().reset_index()

    fig, ax = plt.subplots(figsize=(10, 6))
    ax.plot(revenue["month"], revenue["amount"], marker="o", linewidth=2.5,
            color="#2196F3", markerfacecolor="#FF5722", markersize=8)
    ax.fill_between(range(len(revenue)), revenue["amount"], alpha=0.15, color="#2196F3")
    ax.set_xlabel("Month")
    ax.set_ylabel("Revenue (₹)")
    ax.set_title("Monthly Revenue Trend")
    plt.xticks(rotation=45, ha="right")
    for i, row in revenue.iterrows():
        ax.annotate(f"₹{row['amount']:,.0f}", (i, row["amount"]),
                     textcoords="offset points", xytext=(0, 10), ha="center", fontsize=9)
    _save(fig, "04_monthly_revenue_trend.png")


def chart_top5_most_serviced_vehicles(tables: dict):
    """5. Bar chart – Top 5 Most Serviced Vehicles."""
    srv = tables["Service_Record"]
    veh = tables["Vehicle"]
    merged = srv.merge(veh, on="vehicle_id", how="left")
    top5 = merged["vehicle_number"].value_counts().head(5)

    fig, ax = plt.subplots(figsize=(10, 6))
    sns.barplot(x=top5.index, y=top5.values, hue=top5.index, palette="rocket", ax=ax, edgecolor="black", legend=False)
    ax.set_xlabel("Vehicle Number")
    ax.set_ylabel("Number of Services")
    ax.set_title("Top 5 Most Serviced Vehicles")
    for p in ax.patches:
        ax.annotate(f"{int(p.get_height())}", (p.get_x() + p.get_width() / 2, p.get_height()),
                     ha="center", va="bottom", fontsize=11, fontweight="bold")
    _save(fig, "05_top5_most_serviced_vehicles.png")


def chart_cost_correlation_heatmap(tables: dict):
    """6. Heatmap – Correlation between labor, parts, and total cost."""
    srv = tables["Service_Record"]
    cost_cols = srv[["labor_cost", "parts_cost", "total_cost"]]
    corr = cost_cols.corr()

    fig, ax = plt.subplots(figsize=(7, 6))
    sns.heatmap(corr, annot=True, fmt=".2f", cmap="YlOrRd", linewidths=1,
                square=True, ax=ax, cbar_kws={"shrink": 0.8},
                xticklabels=["Labor Cost", "Parts Cost", "Total Cost"],
                yticklabels=["Labor Cost", "Parts Cost", "Total Cost"])
    ax.set_title("Cost Correlation Heatmap")
    _save(fig, "06_cost_correlation_heatmap.png")


def chart_service_cost_distribution(tables: dict):
    """7. Box plot – Distribution of Service Costs."""
    srv = tables["Service_Record"]

    fig, ax = plt.subplots(figsize=(10, 6))
    data_to_plot = [srv["labor_cost"], srv["parts_cost"], srv["total_cost"]]
    try:
        bp = ax.boxplot(data_to_plot, patch_artist=True, tick_labels=["Labor Cost", "Parts Cost", "Total Cost"],
                        notch=True, widths=0.5)
    except TypeError:
        bp = ax.boxplot(data_to_plot, patch_artist=True, labels=["Labor Cost", "Parts Cost", "Total Cost"],
                        notch=True, widths=0.5)
    colors_box = ["#66c2a5", "#fc8d62", "#8da0cb"]
    for patch, color in zip(bp["boxes"], colors_box):
        patch.set_facecolor(color)
        patch.set_alpha(0.8)
    ax.set_ylabel("Cost (₹)")
    ax.set_title("Distribution of Service Costs")
    _save(fig, "07_service_cost_distribution.png")


def chart_labor_vs_parts_stacked(tables: dict):
    """8. Stacked bar – Labor Cost vs Parts Cost by Vehicle."""
    srv = tables["Service_Record"]
    veh = tables["Vehicle"]
    merged = srv.merge(veh, on="vehicle_id", how="left")
    grouped = merged.groupby("vehicle_number")[["labor_cost", "parts_cost"]].sum()

    fig, ax = plt.subplots(figsize=(10, 6))
    grouped.plot.bar(stacked=True, ax=ax, color=["#4CAF50", "#FF9800"], edgecolor="black", linewidth=0.5)
    ax.set_xlabel("Vehicle Number")
    ax.set_ylabel("Cost (₹)")
    ax.set_title("Labor Cost vs Parts Cost by Vehicle")
    ax.legend(["Labor Cost", "Parts Cost"], loc="upper right")
    plt.xticks(rotation=45, ha="right")
    _save(fig, "08_labor_vs_parts_by_vehicle.png")


# ──────────────────────────────────────────────
# Main
# ──────────────────────────────────────────────

def main():
    print("=" * 60)
    print("  Vehicle Service Analysis – MySQL Edition")
    print("=" * 60)

    # 1. Connect & load
    print("\n[1/4] Connecting to database …")
    conn = get_connection()
    tables = load_all_data(conn)
    conn.close()

    # 2. Preprocess
    print("\n[2/4] Preprocessing data …")
    tables = preprocess(tables)
    print("  ✓ Preprocessing complete")

    # 3. Summary
    print("\n[3/4] Computing summary statistics …")
    print_summary(tables)

    # 4. Generate charts
    print("[4/4] Generating charts …")
    chart_maintenance_cost_by_vehicle(tables)
    chart_services_by_month(tables)
    chart_service_type_distribution(tables)
    chart_monthly_revenue_trend(tables)
    chart_top5_most_serviced_vehicles(tables)
    chart_cost_correlation_heatmap(tables)
    chart_service_cost_distribution(tables)
    chart_labor_vs_parts_stacked(tables)

    print(f"\n✅ All charts saved to: {OUTPUT_DIR}")
    print("=" * 60)


if __name__ == "__main__":
    main()
