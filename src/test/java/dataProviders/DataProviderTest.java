package dataProviders;

import org.testng.annotations.DataProvider;

public class DataProviderTest {

    // ==================== Scenario 3 - Cart Quantity ====================
    @DataProvider(name = "cartQuantityProducts")
    public Object[][] cartQuantityProducts() {
        return new Object[][] {
                {"https://www.jumia.com.eg/kisonli-kisonlii-headphone-wireless-a9-foldable-headset-bluetooth-5.3-hifi-stereo-noise-reduction-black-134538501.html"},
        };
    }

//    // ==================== Scenario 2 - Product Comparison ====================
//    @DataProvider(name = "comparisonProducts")
//    public Object[][] comparisonProducts() {
//        return new Object[][] {
//                {
//                        "https://www.jumia.com.eg/product1.html",
//                        "https://www.jumia.com.eg/product2.html",
//                        "https://www.jumia.com.eg/product3.html"
//                }
//        };
//    }
//
//    // ==================== Scenario 4 - Mixed Cart ====================
//    @DataProvider(name = "mixedCartProducts")
//    public Object[][] mixedCartProducts() {
//        return new Object[][] {
//                {
//                        "https://www.jumia.com.eg/mobile-phone.html",
//                        "https://www.jumia.com.eg/headphones.html",
//                        "https://www.jumia.com.eg/power-bank.html"
//                }
//        };
//    }

    // ==================== General Search Keywords ====================
    @DataProvider(name = "searchKeywords")
    public Object[][] searchKeywords() {
        return new Object[][] {
                {"Samsung Mobile"},
                {"Wireless Headphones"},
                {"Laptop"},
                {"Power Bank"}
        };
    }
}