package com.example.xianhuashangdian.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import androidx.annotation.Nullable;

import com.example.xianhuashangdian.model.CartItem;
import com.example.xianhuashangdian.model.Coupon;
import com.example.xianhuashangdian.model.LotteryResult;
import com.example.xianhuashangdian.model.Merchant;
import com.example.xianhuashangdian.model.OrderCreationResult;
import com.example.xianhuashangdian.model.OrderLine;
import com.example.xianhuashangdian.model.OrderRecord;
import com.example.xianhuashangdian.model.Product;
import com.example.xianhuashangdian.util.PasswordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "flower_shop.db";
    private static final int DB_VERSION = 2;
    public static final int DAILY_DRAW_LIMIT = 3;
    private static DatabaseHelper instance;
    private final Random random = new Random();

    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                "password_hash TEXT NOT NULL, " +
                "security_answer_hash TEXT NOT NULL, " +
                "created_at TEXT NOT NULL)");

        db.execSQL("CREATE TABLE products (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "category TEXT NOT NULL, " +
                "image_key TEXT NOT NULL, " +
                "accent_color TEXT NOT NULL, " +
                "price REAL NOT NULL CHECK(price > 0), " +
                "stock INTEGER NOT NULL CHECK(stock >= 0), " +
                "description TEXT NOT NULL, " +
                "flower_language TEXT NOT NULL)");

        db.execSQL("CREATE TABLE merchants (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "category TEXT NOT NULL, " +
                "specialty TEXT NOT NULL, " +
                "address TEXT NOT NULL, " +
                "phone TEXT NOT NULL, " +
                "rating REAL NOT NULL, " +
                "min_price REAL NOT NULL, " +
                "badge TEXT NOT NULL, " +
                "accent_color TEXT NOT NULL)");

        db.execSQL("CREATE TABLE coupons (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "coupon_code TEXT NOT NULL UNIQUE, " +
                "title TEXT NOT NULL, " +
                "amount REAL NOT NULL CHECK(amount > 0), " +
                "status TEXT NOT NULL, " +
                "created_at TEXT NOT NULL, " +
                "used_at TEXT, " +
                "order_id INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES users(_id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE lottery_draws (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "draw_date TEXT NOT NULL, " +
                "result_title TEXT NOT NULL, " +
                "coupon_amount REAL NOT NULL, " +
                "created_at TEXT NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES users(_id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE cart_items (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "quantity INTEGER NOT NULL CHECK(quantity > 0), " +
                "UNIQUE(user_id, product_id), " +
                "FOREIGN KEY(user_id) REFERENCES users(_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(product_id) REFERENCES products(_id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE orders (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_no TEXT NOT NULL UNIQUE, " +
                "user_id INTEGER NOT NULL, " +
                "payment_method TEXT NOT NULL, " +
                "total_amount REAL NOT NULL, " +
                "discount_amount REAL NOT NULL DEFAULT 0, " +
                "coupon_id INTEGER, " +
                "status TEXT NOT NULL, " +
                "created_at TEXT NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES users(_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(coupon_id) REFERENCES coupons(_id) ON DELETE SET NULL)");

        db.execSQL("CREATE TABLE order_items (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "product_name TEXT NOT NULL, " +
                "unit_price REAL NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "subtotal REAL NOT NULL, " +
                "FOREIGN KEY(order_id) REFERENCES orders(_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(product_id) REFERENCES products(_id) ON DELETE RESTRICT)");

        seedProducts(db);
        seedMerchants(db);
        seedDemoUser(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS merchants (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL UNIQUE, " +
                    "category TEXT NOT NULL, " +
                    "specialty TEXT NOT NULL, " +
                    "address TEXT NOT NULL, " +
                    "phone TEXT NOT NULL, " +
                    "rating REAL NOT NULL, " +
                    "min_price REAL NOT NULL, " +
                    "badge TEXT NOT NULL, " +
                    "accent_color TEXT NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS coupons (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "coupon_code TEXT NOT NULL UNIQUE, " +
                    "title TEXT NOT NULL, " +
                    "amount REAL NOT NULL CHECK(amount > 0), " +
                    "status TEXT NOT NULL, " +
                    "created_at TEXT NOT NULL, " +
                    "used_at TEXT, " +
                    "order_id INTEGER, " +
                    "FOREIGN KEY(user_id) REFERENCES users(_id) ON DELETE CASCADE)");

            db.execSQL("CREATE TABLE IF NOT EXISTS lottery_draws (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "draw_date TEXT NOT NULL, " +
                    "result_title TEXT NOT NULL, " +
                    "coupon_amount REAL NOT NULL, " +
                    "created_at TEXT NOT NULL, " +
                    "FOREIGN KEY(user_id) REFERENCES users(_id) ON DELETE CASCADE)");

            db.execSQL("ALTER TABLE orders ADD COLUMN discount_amount REAL NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE orders ADD COLUMN coupon_id INTEGER");
            seedMerchants(db);
        }
    }

    private void seedProducts(SQLiteDatabase db) {
        insertProduct(db, "红玫瑰", "玫瑰", "red_rose", "#C62845", 12.8, 120,
                "花枝饱满、颜色浓郁，适合纪念日与浪漫告白。",
                "热烈真挚的爱");
        insertProduct(db, "紫玫瑰", "玫瑰", "purple_rose", "#7C4D9E", 15.8, 88,
                "紫色花瓣优雅克制，给特别的人一份独特心意。",
                "珍贵独特的喜欢");
        insertProduct(db, "康乃馨", "康乃馨", "carnation", "#E66A86", 8.8, 150,
                "柔和花瓣搭配清新绿意，温柔耐看。",
                "温暖、感恩与祝福");
        insertProduct(db, "菊花", "菊花", "chrysanthemum", "#E9A62B", 6.8, 180,
                "明快花型带来秋日色彩，适合探望与日常装点。",
                "从容、淡泊与思念");
        insertProduct(db, "茉莉花", "茉莉", "jasmine", "#F5F1E8", 5.8, 200,
                "洁白小巧，清香柔和，适合送给温柔而坚定的人。",
                "纯洁与忠贞");
    }

    private void insertProduct(SQLiteDatabase db, String name, String category, String imageKey,
                               String color, double price, int stock, String description,
                               String flowerLanguage) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("image_key", imageKey);
        values.put("accent_color", color);
        values.put("price", price);
        values.put("stock", stock);
        values.put("description", description);
        values.put("flower_language", flowerLanguage);
        db.insert("products", null, values);
    }

    private void seedMerchants(SQLiteDatabase db) {
        insertMerchant(db, "花间集·玫瑰工坊", "玫瑰", "红玫瑰、紫玫瑰花束",
                "厦门市思明区花屿路18号", "0592-6688001", 4.9, 29.0,
                "告白优选", "#B3264B");
        insertMerchant(db, "暖馨花房", "康乃馨", "康乃馨、长辈感恩花束",
                "厦门市湖里区禾山路36号", "0592-6688002", 4.8, 25.0,
                "亲情推荐", "#D85A7A");
        insertMerchant(db, "清雅花艺", "菊花/茉莉", "菊花、茉莉、素雅花篮",
                "厦门市思明区文屏路12号", "0592-6688003", 4.7, 22.0,
                "清新淡雅", "#2E7D5B");
        insertMerchant(db, "告白花店", "玫瑰/茉莉", "浪漫花束、生日混搭",
                "厦门市集美区杏林湾路55号", "0592-6688004", 4.8, 35.0,
                "同城速送", "#7C4D9E");
        insertMerchant(db, "四季花坊", "康乃馨/菊花", "感恩祝福、探访慰问",
                "厦门市思明区湖滨南路88号", "0592-6688005", 4.6, 20.0,
                "口碑商家", "#C07A22");
    }

    private void insertMerchant(SQLiteDatabase db, String name, String category,
                                String specialty, String address, String phone,
                                double rating, double minPrice, String badge, String color) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("specialty", specialty);
        values.put("address", address);
        values.put("phone", phone);
        values.put("rating", rating);
        values.put("min_price", minPrice);
        values.put("badge", badge);
        values.put("accent_color", color);
        db.insertWithOnConflict("merchants", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void seedDemoUser(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("username", "demo");
        values.put("password_hash", PasswordUtils.hashPassword("demo", "123456a"));
        values.put("security_answer_hash", PasswordUtils.hashAnswer("鲜花"));
        values.put("created_at", now());
        db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long registerUser(String username, String password, String securityAnswer) {
        ContentValues values = new ContentValues();
        values.put("username", username.trim());
        values.put("password_hash", PasswordUtils.hashPassword(username, password));
        values.put("security_answer_hash", PasswordUtils.hashAnswer(securityAnswer));
        values.put("created_at", now());
        return getWritableDatabase().insertWithOnConflict(
                "users", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long authenticateUser(String username, String password) {
        String sql = "SELECT _id FROM users WHERE username = ? AND password_hash = ?";
        String[] args = {
                username.trim(),
                PasswordUtils.hashPassword(username, password)
        };
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, args)) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        }
        return -1L;
    }

    public boolean userExists(String username) {
        try (Cursor cursor = getReadableDatabase().query(
                "users", new String[]{"_id"}, "username = ?",
                new String[]{username.trim()}, null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    public boolean resetPassword(String username, String securityAnswer, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("password_hash", PasswordUtils.hashPassword(username, newPassword));
        int updated = getWritableDatabase().update(
                "users",
                values,
                "username = ? AND security_answer_hash = ?",
                new String[]{username.trim(), PasswordUtils.hashAnswer(securityAnswer)});
        return updated > 0;
    }

    public List<Product> searchProducts(@Nullable String query) {
        String normalized = query == null ? "" : query.trim();
        String sql;
        String[] args;
        if (normalized.isEmpty()) {
            sql = "SELECT * FROM products ORDER BY _id";
            args = null;
        } else {
            String contains = "%" + normalized + "%";
            sql = "SELECT * FROM products " +
                    "WHERE name LIKE ? OR category LIKE ? OR flower_language LIKE ? OR description LIKE ? " +
                    "ORDER BY CASE WHEN name = ? THEN 0 WHEN name LIKE ? THEN 1 ELSE 2 END, _id";
            args = new String[]{contains, contains, contains, contains,
                    normalized, normalized + "%"};
        }

        List<Product> products = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, args)) {
            while (cursor.moveToNext()) {
                products.add(readProduct(cursor));
            }
        }
        return products;
    }

    public Product getProduct(long productId) {
        try (Cursor cursor = getReadableDatabase().query(
                "products", null, "_id = ?",
                new String[]{String.valueOf(productId)},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                return readProduct(cursor);
            }
        }
        return null;
    }

    public List<Product> getProductsByNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder placeholderBuilder = new StringBuilder("?");
        for (int i = 1; i < names.size(); i++) {
            placeholderBuilder.append(",?");
        }
        String placeholders = placeholderBuilder.toString();
        StringBuilder orderSql = new StringBuilder(
                "SELECT * FROM products WHERE name IN (" + placeholders + ") ORDER BY CASE name");
        for (int i = 0; i < names.size(); i++) {
            orderSql.append(" WHEN ? THEN ").append(i);
        }
        orderSql.append(" ELSE ").append(names.size()).append(" END");

        List<String> args = new ArrayList<>(names);
        args.addAll(names);
        List<Product> products = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(
                orderSql.toString(), args.toArray(new String[0]))) {
            while (cursor.moveToNext()) {
                products.add(readProduct(cursor));
            }
        }
        return products;
    }

    public List<Merchant> getRecommendedMerchants(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder where = new StringBuilder();
        List<String> args = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            if (i > 0) {
                where.append(" OR ");
            }
            where.append("category LIKE ? OR specialty LIKE ?");
            String category = "%" + categories.get(i) + "%";
            args.add(category);
            args.add(category);
        }
        List<Merchant> merchants = new ArrayList<>();
        String sql = "SELECT * FROM merchants WHERE " + where +
                " ORDER BY rating DESC, min_price ASC LIMIT 4";
        try (Cursor cursor = getReadableDatabase().rawQuery(
                sql, args.toArray(new String[0]))) {
            while (cursor.moveToNext()) {
                merchants.add(readMerchant(cursor));
            }
        }
        return merchants;
    }

    public long addToCart(long userId, long productId, int quantity) {
        Product product = getProduct(productId);
        if (product == null || quantity < 1 || quantity > product.getStock()) {
            return -1L;
        }

        int currentQuantity = getCartQuantity(userId, productId);
        int finalQuantity = currentQuantity + quantity;
        if (finalQuantity > product.getStock()) {
            return -1L;
        }

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("product_id", productId);
        values.put("quantity", finalQuantity);
        return getWritableDatabase().insertWithOnConflict(
                "cart_items", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean updateCartQuantity(long userId, long productId, int quantity) {
        Product product = getProduct(productId);
        if (product == null || quantity < 1 || quantity > product.getStock()) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);
        int updated = getWritableDatabase().update(
                "cart_items", values, "user_id = ? AND product_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        return updated > 0;
    }

    public int getCartQuantity(long userId, long productId) {
        String sql = "SELECT quantity FROM cart_items WHERE user_id = ? AND product_id = ?";
        try (Cursor cursor = getReadableDatabase().rawQuery(
                sql, new String[]{String.valueOf(userId), String.valueOf(productId)})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    public List<CartItem> getCartItems(long userId) {
        String sql = "SELECT p.*, c.quantity AS cart_quantity " +
                "FROM cart_items c INNER JOIN products p ON p._id = c.product_id " +
                "WHERE c.user_id = ? ORDER BY c._id";
        List<CartItem> items = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(
                sql, new String[]{String.valueOf(userId)})) {
            while (cursor.moveToNext()) {
                Product product = readProduct(cursor);
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("cart_quantity"));
                items.add(new CartItem(product, quantity));
            }
        }
        return items;
    }

    public int getCartItemCount(long userId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE user_id = ?";
        try (Cursor cursor = getReadableDatabase().rawQuery(
                sql, new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    public double getCartTotal(long userId) {
        String sql = "SELECT COALESCE(SUM(p.price * c.quantity), 0) " +
                "FROM cart_items c INNER JOIN products p ON p._id = c.product_id " +
                "WHERE c.user_id = ?";
        try (Cursor cursor = getReadableDatabase().rawQuery(
                sql, new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
        }
        return 0;
    }

    public boolean removeCartItem(long userId, long productId) {
        int deleted = getWritableDatabase().delete(
                "cart_items", "user_id = ? AND product_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        return deleted > 0;
    }

    public OrderCreationResult createOrder(long userId, String paymentMethod) {
        return createOrder(userId, paymentMethod, -1L);
    }

    public OrderCreationResult createOrder(long userId, String paymentMethod, long couponId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            List<CartItem> items = getCartItems(userId);
            if (items.isEmpty()) {
                return OrderCreationResult.failure("购物组合为空");
            }

            double total = 0;
            for (CartItem item : items) {
                Product latest = getProduct(item.getProduct().getId());
                if (latest == null || item.getQuantity() > latest.getStock()) {
                    String name = latest == null ? item.getProduct().getName() : latest.getName();
                    return OrderCreationResult.failure(name + "库存不足");
                }
                total += latest.getPrice() * item.getQuantity();
            }

            Coupon coupon = null;
            double discount = 0;
            if (couponId > 0) {
                coupon = getUsableCoupon(userId, couponId);
                if (coupon == null) {
                    return OrderCreationResult.failure("代金券不可用或已被使用");
                }
                discount = Math.min(coupon.getAmount(), total);
            }
            double payable = Math.max(0, total - discount);

            String orderNo = generateOrderNo();
            ContentValues orderValues = new ContentValues();
            orderValues.put("order_no", orderNo);
            orderValues.put("user_id", userId);
            orderValues.put("payment_method", paymentMethod);
            orderValues.put("total_amount", payable);
            orderValues.put("discount_amount", discount);
            if (coupon != null) {
                orderValues.put("coupon_id", coupon.getId());
            }
            orderValues.put("status", "PAID");
            orderValues.put("created_at", now());
            long orderId = db.insertOrThrow("orders", null, orderValues);

            for (CartItem item : items) {
                Product latest = getProduct(item.getProduct().getId());
                if (latest == null) {
                    continue;
                }
                ContentValues itemValues = new ContentValues();
                itemValues.put("order_id", orderId);
                itemValues.put("product_id", latest.getId());
                itemValues.put("product_name", latest.getName());
                itemValues.put("unit_price", latest.getPrice());
                itemValues.put("quantity", item.getQuantity());
                itemValues.put("subtotal", latest.getPrice() * item.getQuantity());
                db.insertOrThrow("order_items", null, itemValues);

                ContentValues stockValues = new ContentValues();
                stockValues.put("stock", latest.getStock() - item.getQuantity());
                db.update("products", stockValues, "_id = ?",
                        new String[]{String.valueOf(latest.getId())});
            }

            if (coupon != null) {
                ContentValues couponValues = new ContentValues();
                couponValues.put("status", "USED");
                couponValues.put("used_at", now());
                couponValues.put("order_id", orderId);
                int updated = db.update(
                        "coupons",
                        couponValues,
                        "_id = ? AND user_id = ? AND status = ?",
                        new String[]{
                                String.valueOf(coupon.getId()),
                                String.valueOf(userId),
                                "UNUSED"
                        });
                if (updated != 1) {
                    return OrderCreationResult.failure("代金券状态已变化，请重新选择");
                }
            }

            db.delete("cart_items", "user_id = ?", new String[]{String.valueOf(userId)});
            db.setTransactionSuccessful();
            return OrderCreationResult.success(orderId, orderNo);
        } catch (Exception exception) {
            return OrderCreationResult.failure(
                    exception.getMessage() == null ? "订单创建失败" : exception.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public List<OrderRecord> getOrders(long userId) {
        String sql = "SELECT o.*, " +
                "COALESCE((SELECT GROUP_CONCAT(oi.product_name || ' ×' || oi.quantity, '、') " +
                "FROM order_items oi WHERE oi.order_id = o._id), '') AS items_summary " +
                "FROM orders o WHERE o.user_id = ? ORDER BY o._id DESC";
        List<OrderRecord> orders = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(
                sql, new String[]{String.valueOf(userId)})) {
            while (cursor.moveToNext()) {
                orders.add(new OrderRecord(
                        cursor.getLong(cursor.getColumnIndexOrThrow("_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("order_no")),
                        cursor.getString(cursor.getColumnIndexOrThrow("payment_method")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("discount_amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("status")),
                        cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                        cursor.getString(cursor.getColumnIndexOrThrow("items_summary"))));
            }
        }
        return orders;
    }

    public List<OrderLine> getOrderLines(long orderId) {
        List<OrderLine> lines = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                "order_items", null, "order_id = ?",
                new String[]{String.valueOf(orderId)}, null, null, "_id")) {
            while (cursor.moveToNext()) {
                lines.add(new OrderLine(
                        cursor.getString(cursor.getColumnIndexOrThrow("product_name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("unit_price")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal"))));
            }
        }
        return lines;
    }

    public int getTodayDrawCount(long userId) {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM lottery_draws WHERE user_id = ? AND draw_date = ?",
                new String[]{String.valueOf(userId), today()})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    public LotteryResult drawLottery(long userId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int drawCount = getTodayDrawCount(userId);
            if (drawCount >= DAILY_DRAW_LIMIT) {
                return LotteryResult.failure("今天的 3 次抽奖机会已用完");
            }

            int sectorIndex = random.nextInt(10);
            String title;
            double amount;
            switch (sectorIndex) {
                case 0:
                case 4:
                    title = "20元代金券";
                    amount = 20;
                    break;
                case 2:
                case 6:
                    title = "15元代金券";
                    amount = 15;
                    break;
                case 3:
                case 7:
                case 9:
                    title = "5元代金券";
                    amount = 5;
                    break;
                default:
                    title = "谢谢惠顾";
                    amount = 0;
                    break;
            }

            ContentValues drawValues = new ContentValues();
            drawValues.put("user_id", userId);
            drawValues.put("draw_date", today());
            drawValues.put("result_title", title);
            drawValues.put("coupon_amount", amount);
            drawValues.put("created_at", now());
            db.insertOrThrow("lottery_draws", null, drawValues);

            if (amount > 0) {
                ContentValues couponValues = new ContentValues();
                couponValues.put("user_id", userId);
                couponValues.put("coupon_code", generateCouponCode());
                couponValues.put("title", title);
                couponValues.put("amount", amount);
                couponValues.put("status", "UNUSED");
                couponValues.put("created_at", now());
                db.insertOrThrow("coupons", null, couponValues);
            }

            db.setTransactionSuccessful();
            return LotteryResult.success(
                    sectorIndex, title, amount, DAILY_DRAW_LIMIT - drawCount - 1);
        } catch (Exception exception) {
            return LotteryResult.failure(
                    exception.getMessage() == null ? "抽奖失败，请重试" : exception.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public List<Coupon> getUnusedCoupons(long userId) {
        List<Coupon> coupons = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                "coupons",
                null,
                "user_id = ? AND status = ?",
                new String[]{String.valueOf(userId), "UNUSED"},
                null,
                null,
                "amount DESC, _id DESC")) {
            while (cursor.moveToNext()) {
                coupons.add(readCoupon(cursor));
            }
        }
        return coupons;
    }

    private Coupon getUsableCoupon(long userId, long couponId) {
        try (Cursor cursor = getReadableDatabase().query(
                "coupons",
                null,
                "_id = ? AND user_id = ? AND status = ?",
                new String[]{String.valueOf(couponId), String.valueOf(userId), "UNUSED"},
                null,
                null,
                null)) {
            if (cursor.moveToFirst()) {
                return readCoupon(cursor);
            }
        }
        return null;
    }

    private Product readProduct(Cursor cursor) {
        String colorString = cursor.getString(cursor.getColumnIndexOrThrow("accent_color"));
        int color;
        try {
            color = Color.parseColor(colorString);
        } catch (IllegalArgumentException exception) {
            color = Color.parseColor("#B3264B");
        }
        return new Product(
                cursor.getLong(cursor.getColumnIndexOrThrow("_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getString(cursor.getColumnIndexOrThrow("image_key")),
                color,
                cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("flower_language")));
    }

    private Merchant readMerchant(Cursor cursor) {
        String colorString = cursor.getString(cursor.getColumnIndexOrThrow("accent_color"));
        int color;
        try {
            color = Color.parseColor(colorString);
        } catch (IllegalArgumentException exception) {
            color = Color.parseColor("#B3264B");
        }
        return new Merchant(
                cursor.getLong(cursor.getColumnIndexOrThrow("_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getString(cursor.getColumnIndexOrThrow("specialty")),
                cursor.getString(cursor.getColumnIndexOrThrow("address")),
                cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("min_price")),
                cursor.getString(cursor.getColumnIndexOrThrow("badge")),
                color);
    }

    private Coupon readCoupon(Cursor cursor) {
        return new Coupon(
                cursor.getLong(cursor.getColumnIndexOrThrow("_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("coupon_code")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                cursor.getString(cursor.getColumnIndexOrThrow("status")),
                cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
    }

    private String generateOrderNo() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)
                .format(new Date());
        return "FS" + timestamp + (System.nanoTime() % 1000);
    }

    private String generateCouponCode() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA)
                .format(new Date());
        return "CP" + timestamp + (random.nextInt(900) + 100);
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(new Date());
    }

    private String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                .format(new Date());
    }
}
