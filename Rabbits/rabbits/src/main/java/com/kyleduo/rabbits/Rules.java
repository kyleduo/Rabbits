package com.kyleduo.rabbits;

import android.net.Uri;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kyle on 24/01/2018.
 */

public class Rules {

    private static List<Rule> sRules = new ArrayList<>();

    public static void add(String scheme, String domain) {
        sRules.add(Rule.schemes(scheme).setDomains(domain));
    }

    public static void add(String[] schemes, String[] domains) {
        sRules.add(Rule.schemes(schemes).setDomains(domains));
    }

    public static boolean valid(Uri uri) {
        for (Rule rule : sRules) {
            if (rule.valid(uri)) {
                return true;
            }
        }
        return false;
    }

    public static class Rule {

        private Set<String> mSchemes;
        private Set<String> mDomains;

        public static Rule schemes(String... schemes) {
            return new Rule().setSchemes(schemes);
        }

        public static Rule domains(String... domains) {
            return new Rule().setDomains(domains);
        }

        public Rule setDomains(String... domains) {
            if (mDomains == null) {
                mDomains = new HashSet<>(Arrays.asList(domains));
            } else {
                mDomains.addAll(Arrays.asList(domains));
            }
            return this;
        }

        public Rule setSchemes(String... schemes) {
            if (mSchemes == null) {
                mSchemes = new HashSet<>(Arrays.asList(schemes));
            } else {
                mSchemes.addAll(Arrays.asList(schemes));
            }
            return this;
        }

        public boolean valid(Uri uri) {
            return mSchemes.contains(uri.getScheme()) && mDomains.contains(uri.getAuthority());
        }
    }
}
