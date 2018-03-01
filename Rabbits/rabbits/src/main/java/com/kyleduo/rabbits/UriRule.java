package com.kyleduo.rabbits;

import android.net.Uri;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of Rule
 * <p>
 * Created by kyle on 11/02/2018.
 */

public abstract class UriRule implements Rule, Element {

    private Operator mOperator;
    private String mValue;
    private List<String> mValues;

    UriRule() {
    }

    @Override
    public UriRule exists() {
        return operator(Operator.EXISTS, null);
    }

    @Override
    public UriRule is(String value) {
        return operator(Operator.IS, value);
    }

    @Override
    public UriRule startsWith(String value) {
        return operator(Operator.STARTS_WITH, value);
    }

    @Override
    public UriRule endsWith(String value) {
        return operator(Operator.STARTS_WITH, value);
    }

    @Override
    public UriRule in(String... values) {
        mValues = Arrays.asList(values);
        mOperator = Operator.IN;
        return this;
    }

    @Override
    public UriRule contains(String value) {
        return operator(Operator.CONTAINS, value);
    }

    private UriRule operator(Operator operator, String value) {
        mOperator = operator;
        mValue = value;
        return this;
    }

    @Override
    public boolean verify(Action action) {
        return verify(action.getUri());
    }

    public abstract boolean verify(Uri uri);

    boolean verify(String source) {
        switch (mOperator) {
            case EXISTS:
                return source != null && source.length() > 0;
            case IS:
                return source != null && source.equals(mValue);
            case STARTS_WITH:
                return source != null && source.startsWith(mValue);
            case ENDS_WITH:
                return source != null && source.endsWith(mValue);
            case CONTAINS:
                return source != null && source.contains(mValue);
            case IN:
                return source != null && mValues != null && mValues.contains(source);
        }
        return false;
    }

    @Override
    public String toString() {
        return mOperator + " " + mValue + ")";
    }
}
