package com.example.radio;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private int selectedMenuPrice = 0;
    private int totalAddonsPrice = 0;
    private double discountPercentage = 0;
    private String selectedMenuName = "";
    private StringBuilder orderDetails = new StringBuilder();
    private LinearLayout selectedItemsLayout;
    private TextView subtotalPriceView;
    private LinearLayout discountLayout;
    private TextView discountAmountView;
    private TextView totalPriceHeaderView;
    private TextView totalPriceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize views
        selectedItemsLayout = findViewById(R.id.selectedItemsLayout);
        subtotalPriceView = findViewById(R.id.subtotalPrice);
        discountLayout = findViewById(R.id.discountLayout);
        discountAmountView = findViewById(R.id.discountAmount);
        totalPriceHeaderView = findViewById(R.id.totalPriceHeader);
        totalPriceView = findViewById(R.id.totalPrice);
        
        updatePriceBreakdown();
    }

    // Method untuk menangani pemilihan menu (radio button)
    public void onMenuSelected(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        
        if (checked) {
            // Ambil harga dan nama menu dari view
            String priceStr = view.getTag().toString();
            selectedMenuPrice = Integer.parseInt(priceStr);
            selectedMenuName = ((RadioButton) view).getText().toString();
            updateTotalPrice();
        }
    }

    // Method untuk menangani pemilihan tambahan (checkbox)
    public void onAddonSelected(View view) {
        CheckBox checkBox = (CheckBox) view;
        int price = Integer.parseInt(view.getTag().toString());
        String itemName = checkBox.getText().toString();
        
        if (checkBox.isChecked()) {
            totalAddonsPrice += price;
        } else {
            totalAddonsPrice -= price;
        }
        updateTotalPrice();
    }

    // Method untuk mengaplikasikan voucher
    public void onApplyVoucher(View view) {
        TextInputEditText voucherInput = findViewById(R.id.voucherInput);
        String voucherCode = voucherInput.getText().toString().trim();
        TextView voucherStatus = findViewById(R.id.voucherStatus);

        // Cek kode voucher yang valid
        if (voucherCode.equalsIgnoreCase("DISKON10")) {
            discountPercentage = 0.10; // Diskon 10%
        } else if (voucherCode.equalsIgnoreCase("DISKON20")) {
            discountPercentage = 0.20; // Diskon 20%
        } else if (voucherCode.equalsIgnoreCase("DISKON50")) {
            discountPercentage = 0.50; // Diskon 50%
        } else if (!voucherCode.isEmpty()) {
            voucherStatus.setText("Kode voucher tidak valid");
            voucherStatus.setVisibility(View.VISIBLE);
            return;
        } else {
            voucherStatus.setText("Masukkan kode voucher");
            voucherStatus.setVisibility(View.VISIBLE);
            return;
        }

        voucherStatus.setText(String.format("Voucher %d%% berhasil digunakan!", (int)(discountPercentage * 100)));
        voucherStatus.setVisibility(View.VISIBLE);
        updateTotalPrice();
    }

    // Method untuk menangani tombol checkout
    public void onCheckout(View view) {
        if (selectedMenuPrice == 0) {
            Toast.makeText(this, "Silakan pilih menu terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build order details
        orderDetails.setLength(0); // Clear previous order details
        orderDetails.append("Rincian Pesanan:\n");
        orderDetails.append("-------------------\n");
        
        // Add selected menu
        orderDetails.append(selectedMenuName);
        orderDetails.append("\n");
        
        // Add add-ons
        addAddonIfChecked(R.id.addon1);
        addAddonIfChecked(R.id.addon2);
        addAddonIfChecked(R.id.addon3);
        
        // Calculate subtotal
        int subtotal = selectedMenuPrice + totalAddonsPrice;
        double discount = subtotal * discountPercentage;
        double total = subtotal - discount;
        
        // Add price breakdown
        orderDetails.append("\n");
        orderDetails.append("-------------------\n");
        orderDetails.append(String.format("Subtotal: %s\n", formatRupiah(subtotal)));
        
        if (discount > 0) {
            orderDetails.append(String.format("Diskon %d%%: -%s\n", 
                (int)(discountPercentage * 100), 
                formatRupiah(discount)));
        }
        
        orderDetails.append("-------------------\n");
        orderDetails.append(String.format("Total: %s\n\n", formatRupiah(total)));
        orderDetails.append("Terima kasih telah berbelanja!");
        
        // Show order details in a dialog
        new android.app.AlertDialog.Builder(this)
            .setTitle("Rincian Pembayaran")
            .setMessage(orderDetails.toString())
            .setPositiveButton("OK", (dialog, which) -> {
                // Reset form after showing details
                resetForm();
            })
            .show();
    }
    
    // Helper method to add checked add-ons to order details
    private void addAddonIfChecked(int addonId) {
        CheckBox addon = findViewById(addonId);
        if (addon.isChecked()) {
            orderDetails.append("+ " + addon.getText().toString() + "\n");
        }
    }

    // Method untuk menghitung total harga
    private double calculateTotal() {
        double subtotal = selectedMenuPrice + totalAddonsPrice;
        double discount = subtotal * discountPercentage;
        return subtotal - discount;
    }

    // Method untuk mengupdate tampilan rincian harga
    private void updatePriceBreakdown() {
        // Clear previous items
        selectedItemsLayout.removeAllViews();
        
        // Add selected menu
        if (!selectedMenuName.isEmpty()) {
            addItemToBreakdown(selectedMenuName, selectedMenuPrice);
        }
        
        // Add selected add-ons
        addAddonToBreakdown(R.id.addon1);
        addAddonToBreakdown(R.id.addon2);
        addAddonToBreakdown(R.id.addon3);
        
        // Calculate values
        int subtotal = selectedMenuPrice + totalAddonsPrice;
        double discount = subtotal * discountPercentage;
        double total = subtotal - discount;
        
        // Update views
        subtotalPriceView.setText(formatRupiah(subtotal));
        
        if (discount > 0) {
            discountLayout.setVisibility(View.VISIBLE);
            discountAmountView.setText("-" + formatRupiah(discount));
        } else {
            discountLayout.setVisibility(View.GONE);
        }
        
        String formattedTotal = formatRupiah(total);
        totalPriceHeaderView.setText(formattedTotal);
        totalPriceView.setText(formattedTotal);
    }
    
    // Helper method to add an item to the price breakdown
    private void addItemToBreakdown(String name, int price) {
        if (price <= 0) return;
        
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        
        TextView nameView = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f);
        nameView.setLayoutParams(nameParams);
        nameView.setText("• " + name);
        nameView.setTextSize(14);
        
        TextView priceView = new TextView(this);
        priceView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        priceView.setText(formatRupiah(price));
        priceView.setTextSize(14);
        
        itemLayout.addView(nameView);
        itemLayout.addView(priceView);
        
        selectedItemsLayout.addView(itemLayout);
    }
    
    // Helper method to add add-on to breakdown if checked
    private void addAddonToBreakdown(int addonId) {
        CheckBox addon = findViewById(addonId);
        if (addon.isChecked()) {
            int price = Integer.parseInt(addon.getTag().toString());
            String name = "+ " + addon.getText().toString();
            addItemToBreakdown(name, price);
        }
    }
    
    // Method untuk mengupdate tampilan total harga
    private void updateTotalPrice() {
        updatePriceBreakdown();
    }

    // Method untuk format mata uang Rupiah
    private String formatRupiah(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return format.format(amount);
    }

    // Method untuk mereset form
    private void resetForm() {
        // Reset radio buttons
        RadioButton menu1 = findViewById(R.id.menu1);
        RadioButton menu2 = findViewById(R.id.menu2);
        RadioButton menu3 = findViewById(R.id.menu3);
        menu1.setChecked(false);
        menu2.setChecked(false);
        menu3.setChecked(false);

        // Reset checkboxes
        CheckBox addon1 = findViewById(R.id.addon1);
        CheckBox addon2 = findViewById(R.id.addon2);
        CheckBox addon3 = findViewById(R.id.addon3);
        addon1.setChecked(false);
        addon2.setChecked(false);
        addon3.setChecked(false);

        // Reset input voucher
        TextInputEditText voucherInput = findViewById(R.id.voucherInput);
        TextView voucherStatus = findViewById(R.id.voucherStatus);
        voucherInput.setText("");
        voucherStatus.setVisibility(View.GONE);

        // Reset variables
        selectedMenuPrice = 0;
        totalAddonsPrice = 0;
        discountPercentage = 0;
        selectedMenuName = "";
        orderDetails.setLength(0);

        // Update display
        updatePriceBreakdown();
    }
}
