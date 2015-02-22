package com.roote.Utils;

import android.annotation.SuppressLint;


public class LevenshteinCalculator{

        /**
         * Calculates the distance between the identifiers.
         * We ignore the case of the letters in the comparison.
         * @return between 0 (identical) and 1 (completely different)
         */
        public static Number calculateDistance(String s1, String s2) {
                Double result = 1.0;

                if (s1 != null && s2 != null) {
                        result = calculateNormalizedDistance(s1, s2);
                }
                return result;
        }

        /**
         * Calculates the normalized Levenshtein distance.
         * We ignore the case of the letters in the comparison.
         * @return between 0 (identical) and 1 (completely different)
         */
        @SuppressLint("DefaultLocale")
		protected static Double calculateNormalizedDistance(String s1, String s2) {
                Double lDistance = 0.0;
                int max = Math.max(s1.length(), s2.length());
                if (max > 0) {
                        s1 = s1.toLowerCase();
                        s2 = s2.toLowerCase();
                        int distance = Levenshtein.distance(s1, s2);
                        lDistance = distance * 1.0 / max;
                }
                return lDistance;
        }
        public static void main(String[] args) {
        	Double res = (Double) calculateDistance("StarBucks" , "StarBucks Corp Inc");
        	System.out.println(res.toString());
        }
}