"""
Vehicle Service Analysis – Standalone Edition (No MySQL Required)
=================================================================
This script embeds realistic sample data matching the VehicleServiceDB schema
and generates the same 8 professional charts as the MySQL-connected version.
Run it anywhere Python 3 is available with the packages in requirements.txt.

Usage:
    pip install pandas matplotlib seaborn numpy
    python sample_data_standalone.py
"""

import os
import warnings

import numpy as np
import pandas as pd
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import seaborn as sns

# ──────────────────────────────────────────────
# Configuration
# ──────────────────────────────────────────────
OUTPUT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "output")
os.makedirs(OUTPUT_DIR, exist_ok=True)
warnings.filterwarnings("ignore", category=UserWarning)

sns.set_theme(style="whitegrid", palette="Set2", font_scale=1.1)
plt.rcParams.update({
    "figure.dpi": 150,
    "savefig.bbox": "tight",
    "axes.titleweight": "bold",
})

# ──────────────────────────────────────────────
# Embedded sample data
# ──────────────────────────────────────────────

def build_sample_data() -> dict:
    """Return a dict of DataFrames mirroring the VehicleServiceDB tables."""

    # --- 5 Customers ---
    customers = pd.DataFrame({
        "customer_id":   [1, 2, 3, 4, 5],
        "customer_name": ["Rajesh Kumar", "Priya Sharma", "Arun Venkatesh",
                          "Deepa Murugan", "Karthik Rajan"],
        "phone":         ["9876543210", "9123456780", "9988776655",
                          "9871234560", "9900112233"],
        "email":         ["rajesh@email.com", "priya@email.com", "arun@email.com",
                          "deepa@email.com", "karthik@email.com"],
        "address":       ["12 Anna Nagar, Chennai", "45 MG Road, Coimbatore",
                          "78 Kamaraj Salai, Madurai", "23 Nehru St, Trichy",
                          "90 Beach Road, Pondicherry"],
        "created_at":    pd.to_datetime([
            "2024-01-10", "2024-02-15", "2024-03-05",
            "2024-03-20", "2024-04-01"
        ]),
    })

    # --- 6 Vehicles ---
    vehicles = pd.DataFrame({
        "vehicle_id":       [1, 2, 3, 4, 5, 6],
        "customer_id":      [1, 1, 2, 3, 4, 5],
        "vehicle_number":   ["TN01AB1234", "TN01CD5678", "TN38EF9012",
                             "TN58GH3456", "TN45IJ7890", "TN34KL2345"],
        "brand":            ["Maruti Suzuki", "Hyundai", "Toyota",
                             "Tata", "Honda", "Mahindra"],
        "model":            ["Swift", "Creta", "Innova",
                             "Nexon", "City", "XUV700"],
        "vehicle_type":     ["Hatchback", "SUV", "MUV",
                             "SUV", "Sedan", "SUV"],
        "manufacture_year": [2020, 2022, 2019, 2023, 2021, 2022],
    })

    # --- 10 Service Records ---
    service_records = pd.DataFrame({
        "service_id":          list(range(1, 11)),
        "appointment_id":      [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        "vehicle_id":          [1, 2, 3, 4, 5, 6, 1, 3, 2, 5],
        "service_date":        pd.to_datetime([
            "2024-02-10", "2024-02-25", "2024-03-15", "2024-04-05",
            "2024-04-20", "2024-05-10", "2024-06-01", "2024-06-18",
            "2024-07-05", "2024-07-22",
        ]),
        "service_description": [
            "Oil Change", "Brake Pad Replacement", "Full Service",
            "Wheel Alignment", "AC Repair", "Battery Replacement",
            "Oil Change", "Brake Pad Replacement", "Engine Tune-up",
            "Clutch Replacement",
        ],
        "labor_cost":  [800, 1500, 3000, 1200, 2500, 900, 800, 1500, 2000, 3500],
        "parts_cost":  [600, 2500, 4000, 500, 3500, 4500, 600, 2500, 1800, 5000],
        "total_cost":  [1400, 4000, 7000, 1700, 6000, 5400, 1400, 4000, 3800, 8500],
        "next_service_date": pd.to_datetime([
            "2024-08-10", "2024-08-25", "2024-09-15", "2024-10-05",
            "2024-10-20", "2024-11-10", "2024-12-01", "2024-12-18",
            "2025-01-05", "2025-01-22",
        ]),
    })

    # --- Bills ---
    bills = pd.DataFrame({
        "bill_id":        list(range(1, 11)),
        "service_id":     list(range(1, 11)),
        "bill_date":      service_records["service_date"].values,
        "amount":         service_records["total_cost"].values,
        "payment_status": ["PAID", "PAID", "PAID", "PAID", "PENDING",
                           "PAID", "PAID", "PENDING", "PAID", "PENDING"],
        "payment_method": ["Cash", "UPI", "Card", "UPI", "N/A",
                           "Cash", "UPI", "N/A", "Card", "N/A"],
    })

    return {
        "Customer": customers,
        "Vehicle": vehicles,
        "Appointment": pd.DataFrame({          # minimal – not chart-critical
            "appointment_id": list(range(1, 11)),
            "customer_id":    [1, 1, 2, 3, 4, 5, 1, 2, 1, 4],
            "vehicle_id":     service_records["vehicle_id"].values,
            "appointment_date": service_records["service_date"].values,
            "issue_description": service_records["service_description"].values,
            "status": ["COMPLETED"] * 10,
        }),
        "Service_Record": service_records,
        "Bill": bills,
    }


# ──────────────────────────────────────────────
# Summary statistics
# ──────────────────────────────────────────────

def print_summary(tables: dict):
    srv  = tables["Service_Record"]
    bill = tables["Bill"]
    veh  = tables["Vehicle"]
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
    paid    = bill[bill["payment_status"].str.upper() == "PAID"]
    pending = bill[bill["payment_status"].str.upper() == "PENDING"]
    print(f"  Bills PAID                 : {len(paid)}  (₹{paid['amount'].sum():,.2f})")
    print(f"  Bills PENDING              : {len(pending)}  (₹{pending['amount'].sum():,.2f})")
    print("=" * 60 + "\n")


# ──────────────────────────────────────────────
# Chart generators (identical logic to MySQL version)
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
    cost_by_veh = merged.groupby("vehicle_number")["total_cost"].sum().sort_values()

    fig, ax = plt.subplots(figsize=(10, 6))
    colors = sns.color_palette("viridis", len(cost_by_veh))
    cost_by_veh.plot.barh(ax=ax, color=colors, edgecolor="black", linewidth=0.5)
    ax.set_xlabel("Total Maintenance Cost (₹)")
    ax.set_ylabel("Vehicle Number")
    ax.set_title("Maintenance Cost by Vehicle")
    for i, (val, _) in enumerate(zip(cost_by_veh.values, cost_by_veh.index)):
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
        type_counts.values, labels=type_counts.index, autopct="%1.1f%%",
        startangle=140, colors=sns.color_palette("pastel", len(type_counts)),
        pctdistance=0.85, wedgeprops={"edgecolor": "black", "linewidth": 0.5},
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
    corr = srv[["labor_cost", "parts_cost", "total_cost"]].corr()

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
    try:
        bp = ax.boxplot(
            [srv["labor_cost"], srv["parts_cost"], srv["total_cost"]],
            patch_artist=True, tick_labels=["Labor Cost", "Parts Cost", "Total Cost"],
            notch=True, widths=0.5,
        )
    except TypeError:
        bp = ax.boxplot(
            [srv["labor_cost"], srv["parts_cost"], srv["total_cost"]],
            patch_artist=True, labels=["Labor Cost", "Parts Cost", "Total Cost"],
            notch=True, widths=0.5,
        )
    for patch, color in zip(bp["boxes"], ["#66c2a5", "#fc8d62", "#8da0cb"]):
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
    print("  Vehicle Service Analysis – Standalone Edition")
    print("=" * 60)

    # 1. Build sample data
    print("\n[1/3] Building embedded sample data …")
    tables = build_sample_data()
    for name, df in tables.items():
        print(f"  ✓ {name:20s} → {len(df):>4d} rows")

    # 2. Summary
    print("\n[2/3] Computing summary statistics …")
    print_summary(tables)

    # 3. Charts
    print("[3/3] Generating charts …")
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
