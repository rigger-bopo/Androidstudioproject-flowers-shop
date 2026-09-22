package com.example.xianhuashangdian.util;

import com.example.xianhuashangdian.data.DatabaseHelper;
import com.example.xianhuashangdian.model.Merchant;
import com.example.xianhuashangdian.model.Product;
import com.example.xianhuashangdian.model.RecommendationResult;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class FlowerRecommendationEngine {
    private FlowerRecommendationEngine() {
    }

    public static RecommendationResult recommend(DatabaseHelper databaseHelper, String rawInput) {
        String input = rawInput == null ? "" : rawInput.trim().toLowerCase(Locale.CHINA);
        String title;
        String reason;
        List<String> productNames;
        List<String> categories;

        if (containsAny(input, "女朋友", "女友", "老婆", "爱人", "恋人", "表白", "求婚", "浪漫", "爱情")) {
            title = "玫瑰更适合传递爱意";
            reason = "玫瑰象征热烈、专一与浪漫，红玫瑰适合告白，紫玫瑰更显独特与珍贵。";
            productNames = Arrays.asList("红玫瑰", "紫玫瑰");
            categories = Arrays.asList("玫瑰", "玫瑰/茉莉");
        } else if (containsAny(input, "父母", "妈妈", "母亲", "爸爸", "父亲", "长辈", "感恩", "谢谢养育")) {
            title = "康乃馨更适合表达感恩";
            reason = "康乃馨温柔耐看，寓意温暖、感恩与祝福，送父母或长辈更稳妥。";
            productNames = Arrays.asList("康乃馨", "茉莉花");
            categories = Arrays.asList("康乃馨", "康乃馨/菊花");
        } else if (containsAny(input, "老师", "教师", "毕业", "感谢师恩", "导师")) {
            title = "康乃馨与菊花更贴合师生祝福";
            reason = "康乃馨表达感谢，菊花和茉莉清雅端庄，适合教师节、毕业与致谢场景。";
            productNames = Arrays.asList("康乃馨", "菊花", "茉莉花");
            categories = Arrays.asList("菊花/茉莉", "康乃馨/菊花", "康乃馨");
        } else if (containsAny(input, "看望", "探病", "慰问", "康复", "病人")) {
            title = "清新淡雅的花更合适";
            reason = "探病慰问宜选气味轻柔、颜色明亮的茉莉与康乃馨，避免过于浓烈的花束。";
            productNames = Arrays.asList("茉莉花", "康乃馨");
            categories = Arrays.asList("茉莉", "康乃馨");
        } else if (containsAny(input, "纪念", "思念", "缅怀", "清明", "祭奠")) {
            title = "菊花更适合表达追思";
            reason = "菊花寓意从容、淡泊与思念，适合纪念和追思场合。";
            productNames = Arrays.asList("菊花");
            categories = Arrays.asList("菊花", "康乃馨/菊花");
        } else if (containsAny(input, "生日", "朋友", "闺蜜", "同事", "日常", "祝福")) {
            title = "明快混搭更有生日氛围";
            reason = "红玫瑰提升喜庆感，茉莉带来清新气息，适合朋友生日和日常祝福。";
            productNames = Arrays.asList("红玫瑰", "茉莉花", "紫玫瑰");
            categories = Arrays.asList("玫瑰", "茉莉", "玫瑰/茉莉");
        } else {
            title = "先从高人气花材开始挑选";
            reason = "红玫瑰、康乃馨和茉莉覆盖告白、感恩与日常祝福，是店内最常被选择的组合。";
            productNames = Arrays.asList("红玫瑰", "康乃馨", "茉莉花");
            categories = Arrays.asList("玫瑰", "康乃馨", "茉莉");
        }

        List<Product> products = databaseHelper.getProductsByNames(productNames);
        List<Merchant> merchants = databaseHelper.getRecommendedMerchants(categories);
        return new RecommendationResult(title, reason, products, merchants);
    }

    private static boolean containsAny(String input, String... keywords) {
        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
