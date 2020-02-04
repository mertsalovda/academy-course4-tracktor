package com.elegion.tracktor.util;

import javax.annotation.Nonnull;

public class CaloriesUtil {

    public static float execute(int activeType,
                                float averageSpeed,
                                long duration,
                                float weight,
                                int height,
                                @Nonnull String[] activeTypes) {

        float result = 0.0f;
        float M = weight;
        float V = SpeedUtil.convertKmSToMs(averageSpeed);
        float H = height / 100.0f; // height in m
        switch (activeTypes[activeType]) {
            case "Ходьба":
                result = (0.035f * M + (V * V / H) * 0.029f * M) * (duration * 0.0166667f);
                // К = 0,035 х М + (V²/Н) х 0,029 х М, где
                // К – количество калорий, сжигаемых за минуту ходьбы;
                // М – вес тела в килограммах;
                // V – скорость ходьбы в метрах в секунду;
                // Н – рост в метрах.
                break;
            case "Бег":
                result = M * (duration * 0.0166667f);
                // Затраты при беге M(кг)*T(мин)
                break;
            case "Велосипед":
                result = 500 * duration * 0.000277778f;
                // 400-600 ккал/час
                break;
            case "Самокат":
                result = 420 * duration * 0.000277778f;
                // 420 ккал/час
                break;
            default:
                System.err.println("<ОШИБКА>: добавьте расчёт калорий для активности: " + activeTypes[activeType] + " в CaloriesUtil");
                break;
        }
        return result;
    }
}
