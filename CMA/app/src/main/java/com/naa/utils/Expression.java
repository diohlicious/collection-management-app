package com.naa.utils;




import android.util.Log;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

public class Expression {

    public static final double PI = 3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679;
    public static final double e = 2.71828182845904523536028747135266249775724709369995957496696762772407663;

    private Map<String, Operator> operators = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private Map<String, LazyFunction> functions = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private Map<String, Object> variables = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private static final char decimalSeparator = '.';//us format
    private static final char minusSign = '-';
    private static final LazyNumber PARAMS_START = new LazyNumber() {
        public Object eval() {
            return null;
        }
    };
    public static class ExpressionException extends RuntimeException {
        private static final long serialVersionUID = 1118142866870779047L;
        public ExpressionException(String message) {
            super(message);
        }
    }
    private abstract class newLazyNumber  implements LazyNumber{
        private String token;
        private Object data;
        public newLazyNumber (String token){
            this.token = token;
        }
        public newLazyNumber (Object data){
            this.data = data;
        }
        public Object getData(){
            return data;
        }
        public String getToken(){
            return token;
        }
        public abstract Object eval();

    }
    private interface LazyNumber {
        Object eval();
    }
    private abstract class LazyFunction {
        private String name;
        private int numParams;
        public LazyFunction(String name, int numParams) {
            this.name = name.toUpperCase(Locale.ROOT);
            this.numParams = numParams;
        }
        public String getName() {
            return name;
        }

        public int getNumParams() {
            return numParams;
        }

        public boolean numParamsVaries() {
            return numParams < 0;
        }
        public abstract LazyNumber lazyEval(List<LazyNumber> lazyParams);
    }

    private abstract class Function extends LazyFunction {
        public Function(String name, int numParams) {
            super(name, numParams);
        }
        public LazyNumber lazyEval(List<LazyNumber> lazyParams) {
            final List<Object> params = new ArrayList<Object>();
            for (LazyNumber lazyParam : lazyParams) {
                params.add(lazyParam.eval());
            }
            return new LazyNumber() {
                public Object eval() {

                    return Function.this.eval(params);
                }
            };
        }
        public abstract Object eval(List<Object> parameters);
    }
    private abstract class Operator {
        private String oper;
        private int precedence;
        private boolean leftAssoc;
        public Operator(String oper, int precedence, boolean leftAssoc) {
            this.oper = oper;
            this.precedence = precedence;
            this.leftAssoc = leftAssoc;
        }
        public String getOper() {
            return oper;
        }
        public int getPrecedence() {
            return precedence;
        }
        public boolean isLeftAssoc() {
            return leftAssoc;
        }
        public abstract Object eval(Object v1, Object v2);
    }
    private class Tokenizer implements Iterator<String> {
        private int pos = 0;
        private String input;
        private String previousToken;
        public Tokenizer(String input) {
            this.input = input.trim();
        }
        @Override
        public boolean hasNext() {
            return (pos < input.length());
        }
        private char peekNextChar() {
            if (pos < (input.length() - 1)) {
                return input.charAt(pos + 1);
            } else {
                return 0;
            }
        }
        public boolean peakFunction(){
            int xcurr = pos;
            if (xcurr >= input.length()) {
                return false;
            }
            char ch = input.charAt(xcurr);
            while (Character.isWhitespace(ch) && xcurr < input.length()) {
                ch = input.charAt(++xcurr);
            }
            if (ch == '(') {
                return true;
            }
            return false;
        }
        @Override
        public String next() {
            StringBuilder token = new StringBuilder();
            if (pos >= input.length()) {
                return previousToken = null;
            }
            char ch = input.charAt(pos);
            while (Character.isWhitespace(ch) && pos < input.length()) {
                ch = input.charAt(++pos);
            }
            if (Character.isDigit(ch)) {
                while ((Character.isDigit(ch) || ch == decimalSeparator
                        || ch == 'e' || ch == 'E'
                        || (ch == minusSign && token.length() > 0
                        && ('e'==token.charAt(token.length()-1) || 'E'==token.charAt(token.length()-1)))
                        || (ch == '+' && token.length() > 0
                        && ('e'==token.charAt(token.length()-1) || 'E'==token.charAt(token.length()-1)))
                ) && (pos < input.length())) {
                    token.append(input.charAt(pos++));
                    ch = pos == input.length() ? 0 : input.charAt(pos);
                }
            } else if (ch == minusSign
                    && Character.isDigit(peekNextChar())
                    && ("(".equals(previousToken) || ",".equals(previousToken)
                    || previousToken == null || operators
                    .containsKey(previousToken))) {
                token.append(minusSign);
                pos++;
                token.append(next());
            } else if (Character.isLetter(ch) || (ch == '_')  ||  (ch == '$' || ch == '@' || ch == '!')  ) {
                while ((Character.isLetter(ch) || Character.isDigit(ch) || (ch == '_') ||  (ch == '$' || ch == '@' || ch == '!'))
                        && (pos < input.length())) {
                    token.append(input.charAt(pos++));
                    ch = pos == input.length() ? 0 : input.charAt(pos);
                }


            } else if (ch == '(' || ch == ')' || ch == ',') {
                token.append(ch);
                pos++;
            } else {
                while (   ch != '$' && ch != '@' && ch != '!' && !Character.isLetter(ch) && !Character.isDigit(ch)
                        && ch != '_' && !Character.isWhitespace(ch)
                        && ch != '(' && ch != ')' && ch != ','
                        && (pos < input.length())) {
                    token.append(input.charAt(pos));
                    pos++;
                    ch = pos == input.length() ? 0 : input.charAt(pos);
                    if (ch == minusSign) {
                        break;
                    }
                }
                if (!operators.containsKey(token.toString())) {
                    throw new ExpressionException("Unknown operator '" + token
                            + "' at position " + (pos - token.length() + 1));
                }
            }
            return previousToken = token.toString();
        }

        public void remove() {
            throw new ExpressionException("remove() not supported");
        }
        public int getPos() {
            return pos;
        }

    }
    private Map<String, String> varstrings = new HashMap();
    private int findquote(String expression, int pos, boolean squote){
        boolean hadSlash = false;
        for (int i = pos+1; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (hadSlash) {
                hadSlash = false;
                switch (ch) {
                    case '\'':
                    case '\"':
                    case '\\':
                    case 'r':
                    case 'f':
                    case 't':
                    case 'n':
                    case 'b':
                        break;//switch
                    default :
                        return expression.length();//error
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            if (squote && expression.charAt(i)=='\'') {
                return i;
            }else if (!squote && expression.charAt(i)=='"') {
                return i;
            }
        }
        return expression.length();
    }

    private String group(String expression){
        expression = expression.replace("}", ") ");
        expression = expression.replace("{", " exec(");
        expression = expression.replace(";", " , ");
        return expression;
    }
    private String quote(String expression){
        varstrings.clear();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            switch (expression.charAt(i)) {
                case '"':
                {
                    int l = findquote(expression, i, false);
                    if (l > i && l < expression.length()) {
                        stringBuilder.append(getVarQuoteName(unEscapeSQL(expression.substring(i+1, l)), i));
                    }       i = l;
                    break;
                }
                case '\'':
                {
                    int l = findquote(expression, i, true);
                    if (l > i && l < expression.length()) {
                        stringBuilder.append(getVarQuoteName(unEscapeSQL(expression.substring(i+1, l)), i));
                    }       i = l;
                    break;
                }
                default:
                    stringBuilder.append(expression.charAt(i));
                    break;
            }
        }
        return stringBuilder.toString();
    }
    private String getVarQuoteName(String value, int hs){
        for (String key : varstrings.keySet()) {
            if (varstrings.get(key).equalsIgnoreCase(value)) {
                return key;
            }
        }
        String varstrname = "$svar$"+hs;
        varstrings.put(varstrname, value);
        return varstrname;
    }
    private String unEscapeSQL(String string){
        StringBuilder builder = new StringBuilder();
        boolean hadSlash = false;
        for (int i = 0; i < string.length(); i++) {
                /*
                if (squote && expression.charAt(i)=='\'') {
                    return i;
                }else if (!squote && expression.charAt(i)=='"') {
                    return i;
                }
                */
            char ch = string.charAt(i);
            if (hadSlash) {
                hadSlash = false;
                switch (ch) {
                    case '\'':
                        builder.append(ch);
                        break;//switch
                    case '\"':
                        builder.append(ch);
                        break;//switch
                    case '\\':
                        builder.append(ch);
                        break;//switch
                    case 'r':
                        builder.append('\r');
                        break;//switch
                    case 'f':
                        builder.append('\f');
                        break;//switch
                    case 't':
                        builder.append('\t');
                        break;//switch
                    case 'n':
                        builder.append('\n');
                        break;//switch
                    case 'b':
                        builder.append('\b');
                        break;//switch
                    default :
                        return "";//error
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            builder.append(ch);
        }
        return builder.toString();
    }
    public Expression() {
        addOperator(new Operator("+", 20, true) {
            public Object eval(Object v1, Object v2) {
                return getDouble(v1)+getDouble(v2);
            }
        });
        addOperator(new Operator("-", 20, true) {
            public Object eval(Object v1, Object v2) {
                return getDouble(v1)-getDouble(v2);
            }
        });
        addOperator(new Operator("*", 30, true) {
            public Object eval(Object v1, Object v2) {
                return getDouble(v1)*getDouble(v2);
            }
        });
        addOperator(new Operator("/", 30, true) {
            public Object eval(Object v1, Object v2) {
                return getDouble(v1)/getDouble(v2);
            }
        });
        addOperator(new Operator("%", 30, true) {
            public Object eval(Object v1, Object v2) {
                return getDouble(v1)%getDouble(v2);
            }
        });
        addOperator(new Operator("^", 40, false) {
            public Object eval(Object v1, Object v2) {
                return Math.pow( getDouble(v1), getDouble(v2) );
            }
        });

        addOperator(new Operator("&&", 4, false) {
            public Object eval(Object v1, Object v2) {
                return Boolean.valueOf(String.valueOf(v1))&& Boolean.valueOf(String.valueOf(v2))  ;
            }
        });
        addOperator(new Operator("and", 4, false) {
            public Object eval(Object v1, Object v2) {
                return Boolean.valueOf(String.valueOf(v1))&& Boolean.valueOf(String.valueOf(v2))  ;
            }
        });
        addOperator(new Operator("||", 2, false) {
            public Object eval(Object v1, Object v2) {
                return Boolean.valueOf(String.valueOf(v1))|| Boolean.valueOf(String.valueOf(v2))  ;
            }
        });
        addOperator(new Operator("or", 2, false) {
            public Object eval(Object v1, Object v2) {
                return Boolean.valueOf(String.valueOf(v1))|| Boolean.valueOf(String.valueOf(v2))  ;
            }
        });
        addOperator(new Operator("like", 10, false) {
            public Object eval(Object v1, Object v2) {
                String l = String.valueOf(v1);
                String r = String.valueOf(v2);
                if (r.startsWith("%")&&r.endsWith("%")) {
                    return l.contains(r.substring(1,r.length()-1));
                }else  if (r.startsWith("%")) {
                    return l.endsWith(r.substring(1));
                }else  if (r.endsWith("%")) {
                    return l.startsWith(r.substring(0, r.length()-1));
                }else{
                    return l.equals(r);
                }
            }
        });
        addOperator(new Operator(">", 10, false) {
            public Object eval(Object v1, Object v2) {
                if (v1 instanceof Date || v2 instanceof Date  ) {

                }
                return getDouble(v1)>getDouble(v2);
            }
        });
        addOperator(new Operator(">=", 10, false) {
            public Object eval(Object v1, Object v2) {
                return getDouble(v1)>=getDouble(v2);
            }
        });
        addOperator(new Operator("<", 10, false) {
            public Object eval(Object v1, Object v2) {
                return getDouble(v1)<getDouble(v2);
            }
        });
        addOperator(new Operator("<", 10, false) {
            public Object eval(Object v1, Object v2) {
                return getDouble(v1)<=getDouble(v2);
            }
        });
        addOperator(new Operator("=", 7, false) {
            public Object eval(Object v1, Object v2) {
                variables.put(String.valueOf(v1), v2);
                if (variableListener!=null) {
                    variableListener.set(String.valueOf(v1), v2);
                }
                //System.out.println("v0:"+String.valueOf(v1));
                //System.out.println("v0:"+String.valueOf(v2));
                return v2;
            }
        });
        addOperator(new Operator("==", 7, false) {
            public Object eval(Object v1, Object v2) {
                /*if (isNumber(String.valueOf(v1)) && isNumber(String.valueOf(v2))) {
                    return getNumber(String.valueOf(v1)).doubleValue() == getNumber(String.valueOf(v2)).doubleValue()  ;
                }*/
                return String.valueOf(v1).equals(String.valueOf(v2)) ;
            }
        });
        addOperator(new Operator("!=", 7, false) {
            public Object eval(Object v1, Object v2) {
                if (isNumber(String.valueOf(v1)) && isNumber(String.valueOf(v2))) {
                    return getNumber(String.valueOf(v1)).doubleValue() != getNumber(String.valueOf(v2)).doubleValue()  ;
                }
                return !String.valueOf(v1).equals(String.valueOf(v2)) ;
            }
        });
        addOperator(new Operator("<>", 7, false) {
            public Object eval(Object v1, Object v2) {
                return String.valueOf(v1).intern()!=String.valueOf(v2).intern();
            }
        });

        //function
        addLazyFunction(new LazyFunction("if", 3) {
            public LazyNumber lazyEval(List<LazyNumber> lazyParams) {
                boolean isTrue = Boolean.valueOf(lazyParams.get(0).eval().toString());
                return isTrue ? lazyParams.get(1) : lazyParams.get(2);
            }
        });
        addFunction(new Function("abs", 1) {
            public Object eval(List<Object> parameters) {
                return Math.abs( getDouble(parameters.get(0)) );
            }
        });
        addFunction(new Function("not", 1) {
            public Object eval(List<Object> parameters) {
                return !Boolean.valueOf(parameters.get(0).toString());
            }
        });
        addFunction(new Function("max", -11) {
            public Object eval(List<Object> parameters) {
                double max = 0;
                for (Object parameter : parameters) {
                    double val = getDouble(parameter);
                    if (max == 0 || (val>max)) {
                        max = val;
                    }
                }
                return max;
            }
        });
        addLazyFunction(new LazyFunction("mov", 2) {
            public LazyNumber lazyEval(List<LazyNumber> lazyParams) {
                if (variableListener!=null) {
                    variableListener.set(String.valueOf(lazyParams.get(0)), String.valueOf(lazyParams.get(1).eval()));
                }else{
                    variables.put(String.valueOf(lazyParams.get(0)), String.valueOf(lazyParams.get(1).eval()));
                }
                return lazyParams.get(0);
            }

        });
        addLazyFunction(new LazyFunction("gen", -1) {
            public LazyNumber lazyEval(List<LazyNumber> lazyParams) {
                Object out = null;
                for (int i = 0; i < lazyParams.size(); i++) {
                    out = lazyParams.get(i).eval() ;
                }
                return new newLazyNumber(out) {
                    public Object eval() {
                        return getData();
                    }
                };
            }
        });
        addLazyFunction(new LazyFunction("exec", -1) {
            public LazyNumber lazyEval(List<LazyNumber> lazyParams) {
                Object out = null;
                for (int i = 0; i < lazyParams.size(); i++) {
                    out = lazyParams.get(i).eval() ;
                }
                return new newLazyNumber(out) {
                    public Object eval() {
                        return getData();
                    }
                };
            }
        });
        addFunction(new Function("eval", 1) {
            public Object eval(List<Object> parameters) {
                return evaluate(String.valueOf(parameters.get(0)), getOnVariableListener());
            }
        });
        addFunction(new Function("random", 0) {
            public Object eval(List<Object> parameters) {
                double d = Math.random();
                return d;
            }
        });
        addFunction(new Function("out", 1) {
            public Object eval(List<Object> parameters) {
                return parameters.get(0);
            }
        });
        addFunction(new Function("now", 0) {
            public Object eval(List<Object> parameters) {
                return Now();
            }
        });
        addFunction(new Function("today", 0) {
            public Object eval(List<Object> parameters) {
                return new Date();
            }
        });
        addFunction(new Function("len", 1) {
            public Object eval(List<Object> parameters) {
                return String.valueOf(parameters.get(0)).length() ;
            }
        });
        addFunction(new Function("length", 1) {
            public Object eval(List<Object> parameters) {
                return String.valueOf(parameters.get(0)).length() ;
            }
        });
        addFunction(new Function("int", 1) {
            public Object eval(List<Object> parameters) {
                return getInt(String.valueOf(parameters.get(0))) ;
            }
        });
        addFunction(new Function("long", 1) {
            public Object eval(List<Object> parameters) {
                return getLong(String.valueOf(parameters.get(0))) ;
            }
        });
        addFunction(new Function("double", 1) {
            public Object eval(List<Object> parameters) {
                return getDouble(String.valueOf(parameters.get(0))) ;
            }
        });
        addFunction(new Function("decimal", 1) {
            public Object eval(List<Object> parameters) {
                return new BigDecimal(String.valueOf(parameters.get(0))).toPlainString() ;
            }
        });
        addFunction(new Function("ucase", 1) {
            public Object eval(List<Object> parameters) {
                return String.valueOf(parameters.get(0)).toUpperCase();
            }
        });
        addFunction(new Function("lcase", 1) {
            public Object eval(List<Object> parameters) {
                return String.valueOf(parameters.get(0)).toLowerCase();
            }
        });
        addFunction(new Function("trim", 1) {
            public Object eval(List<Object> parameters) {
                return String.valueOf(parameters.get(0)).trim();
            }
        });
        addFunction(new Function("concat", -1) {
            public Object eval(List<Object> parameters) {
                StringBuilder builder = new StringBuilder();
                for (Object parameter : parameters) {
                    builder.append(String.valueOf(parameter));
                }
                return builder.toString();
            }
        });
        addFunction(new Function("indexof", -1) {
            @Override
            public Object eval(List<Object> parameters) {
                if (parameters.size()==2) {
                    return String.valueOf(parameters.get(0)).indexOf( String.valueOf(parameters.get(1)) );
                }else if (parameters.size()==3) {
                    return String.valueOf(parameters.get(0)).indexOf( String.valueOf(parameters.get(1)), getInt(String.valueOf(parameters.get(2))) );
                }
                return -1;
            }
        });
        addFunction(new Function("replace", -1) {
            public Object eval(List<Object> parameters) {
                if (parameters.size()==3) {
                    return String.valueOf(parameters.get(0)).replace( String.valueOf(parameters.get(1)), String.valueOf(parameters.get(2)) );
                }
                return parameters.get(0);
            }
        });
        addFunction(new Function("substring", -1) {
            public Object eval(List<Object> parameters) {
                if (parameters.size()==2) {
                    return String.valueOf(parameters.get(0)).substring( getInt(String.valueOf(parameters.get(1))) );
                }else if (parameters.size()==3) {
                    return String.valueOf(parameters.get(0)).substring( getInt(String.valueOf(parameters.get(1))), getInt(String.valueOf(parameters.get(2))) );
                }
                return "";
            }
        });
                /*
		addOperator(new Operator("&&", 4, false) {
			@Override
			public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
				boolean b1 = !v1.equals(BigDecimal.ZERO);
				boolean b2 = !v2.equals(BigDecimal.ZERO);
				return b1 && b2 ? BigDecimal.ONE : BigDecimal.ZERO;
			}
		});
		addOperator(new Operator("||", 2, false) {
			@Override
			public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
				boolean b1 = !v1.equals(BigDecimal.ZERO);
				boolean b2 = !v2.equals(BigDecimal.ZERO);
				return b1 || b2 ? BigDecimal.ONE : BigDecimal.ZERO;
			}
		});
		addOperator(new Operator(">", 10, false) {
			@Override
			public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
				return v1.compareTo(v2) == 1 ? BigDecimal.ONE : BigDecimal.ZERO;
			}
		});
		addOperator(new Operator(">=", 10, false) {
			@Override
			public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
				return v1.compareTo(v2) >= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
			}
		});
		addOperator(new Operator("<", 10, false) {
			@Override
			public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
				return v1.compareTo(v2) == -1 ? BigDecimal.ONE
						: BigDecimal.ZERO;
			}
		});
		addOperator(new Operator("<=", 10, false) {
			@Override
			public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
				return v1.compareTo(v2) <= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
			}
		});
		addOperator(new Operator("=", 7, false) {
			@Override
			public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
				return v1.compareTo(v2) == 0 ? BigDecimal.ONE : BigDecimal.ZERO;
			}
		});
		addOperator(new Operator("==", 7, false) {
			@Override
			public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
				return operators.get("=").eval(v1, v2);
			}
		});
		addOperator(new Operator("!=", 7, false) {
			@Override
			public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
				return v1.compareTo(v2) != 0 ? BigDecimal.ONE : BigDecimal.ZERO;
			}
		});
		addOperator(new Operator("<>", 7, false) {
			@Override
			public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
				return operators.get("!=").eval(v1, v2);
			}

		});
		addFunction(new Function("NOT", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				boolean zero = parameters.get(0).compareTo(BigDecimal.ZERO) == 0;
				return zero ? BigDecimal.ONE : BigDecimal.ZERO;
			}
		});
		addLazyFunction(new LazyFunction("IF", 3) {
			@Override
			public LazyNumber lazyEval(List<LazyNumber> lazyParams) {
				boolean isTrue = !lazyParams.get(0).eval().equals(BigDecimal.ZERO);
				return isTrue ? lazyParams.get(1) : lazyParams.get(2);
			}
		});
		addFunction(new Function("RANDOM", 0) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.random();
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("SIN", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.sin(Math.toRadians(parameters.get(0)
						.doubleValue()));
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("COS", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.cos(Math.toRadians(parameters.get(0)
						.doubleValue()));
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("TAN", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.tan(Math.toRadians(parameters.get(0)
						.doubleValue()));
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("ASIN", 1) { // added by av
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.toDegrees(Math.asin(parameters.get(0)
						.doubleValue()));
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("ACOS", 1) { // added by av
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.toDegrees(Math.acos(parameters.get(0)
						.doubleValue()));
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("ATAN", 1) { // added by av
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.toDegrees(Math.atan(parameters.get(0)
						.doubleValue()));
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("SINH", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.sinh(parameters.get(0).doubleValue());
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("COSH", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.cosh(parameters.get(0).doubleValue());
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("TANH", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.tanh(parameters.get(0).doubleValue());
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("RAD", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.toRadians(parameters.get(0).doubleValue());
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("DEG", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.toDegrees(parameters.get(0).doubleValue());
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("MAX", -1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				if (parameters.size() == 0) {
					throw new ExpressionException("MAX requires at least one parameter");
				}
				BigDecimal max = null;
				for (BigDecimal parameter : parameters) {
					if (max == null || parameter.compareTo(max) > 0) {
						max = parameter;
					}
				}
				return max;
			}
		});
		addFunction(new Function("MIN", -1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				if (parameters.size() == 0) {
					throw new ExpressionException("MIN requires at least one parameter");
				}
				BigDecimal min = null;
				for (BigDecimal parameter : parameters) {
					if (min == null || parameter.compareTo(min) < 0) {
						min = parameter;
					}
				}
				return min;
			}
		});
		addFunction(new Function("ABS", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				return parameters.get(0).abs(mc);
			}
		});
		addFunction(new Function("LOG", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.log(parameters.get(0).doubleValue());
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("LOG10", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				double d = Math.log10(parameters.get(0).doubleValue());
				return new BigDecimal(d, mc);
			}
		});
		addFunction(new Function("ROUND", 2) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				BigDecimal toRound = parameters.get(0);
				int precision = parameters.get(1).intValue();
				return toRound.setScale(precision, mc.getRoundingMode());
			}
		});
		addFunction(new Function("FLOOR", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				BigDecimal toRound = parameters.get(0);
				return toRound.setScale(0, RoundingMode.FLOOR);
			}
		});
		addFunction(new Function("CEILING", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				BigDecimal toRound = parameters.get(0);
				return toRound.setScale(0, RoundingMode.CEILING);
			}
		});

		addFunction(new Function("SQRT", 1) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {

				BigDecimal x = parameters.get(0);
				if (x.compareTo(BigDecimal.ZERO) == 0) {
					return new BigDecimal(0);
				}
				if (x.signum() < 0) {
					throw new ExpressionException(
							"Argument to SQRT() function must not be negative");
				}
				BigInteger n = x.movePointRight(mc.getPrecision() << 1)
						.toBigInteger();

				int bits = (n.bitLength() + 1) >> 1;
				BigInteger ix = n.shiftRight(bits);
				BigInteger ixPrev;

				do {
					ixPrev = ix;
					ix = ix.add(n.divide(ix)).shiftRight(1);
					// Give other threads a chance to work;
					Thread.yield();
				} while (ix.compareTo(ixPrev) != 0);

				return new BigDecimal(ix, mc.getPrecision());
			}
		});
               */

        variables.put("e", e);
        variables.put("PI", PI);
        variables.put("TRUE", true);
        variables.put("FALSE", false);
    }

    private boolean isNumber(String st) {
        if (st.length()==0) return false;
        if (st.charAt(0) == minusSign && st.length() == 1) return false;
        if (st.charAt(0) == '+' && st.length() == 1) return false;
        if (st.charAt(0) == 'e' ||  st.charAt(0) == 'E') return false;
        for (char ch : st.toCharArray()) {
            if (!Character.isDigit(ch) && ch != minusSign
                    && ch != decimalSeparator
                    && ch != 'e' && ch != 'E' && ch != '+')
                return false;
        }
        return true;
    }
    private List<CharSequence> shuntingYard(String expression) {
        List<CharSequence> outputQueue = new ArrayList<CharSequence>();
        Stack<CharSequence> stack = new Stack<CharSequence>();

        Tokenizer tokenizer = new Tokenizer(expression);

        String lastFunction = null;
        String previousToken = null;
        while (tokenizer.hasNext()) {
            String token = tokenizer.next();
            boolean b = false;
            //System.out.println(":"+token);
            // //System.out.println("p:"+tokenizer.peakFunction());
            if (isNumber(token)) {
                outputQueue.add(token);
            } else if (! (b = tokenizer.peakFunction()) &&(variables.containsKey(token)||varstrings.containsKey(token))) {
                outputQueue.add(token);
            } else if (b && functions.containsKey(token.toUpperCase(Locale.ROOT))) {
                stack.push(new FunctionString(token.toString()));
                //stack.push(token);
                lastFunction = token;
            } else if (token.charAt(0) == '$' || token.charAt(0) == '@'|| token.charAt(0) == '!') {
                outputQueue.add(token);
            } else if (",".equals(token)) {
                if (operators.containsKey(previousToken)) {
                    throw new ExpressionException("Missing parameter(s) for operator " + previousToken +
                            " at character position " + (tokenizer.getPos() - 1 - previousToken.length()));
                }
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    outputQueue.add(stack.pop());
                }
                if (stack.isEmpty()) {
                    throw new ExpressionException("Parse error for function '"
                            + lastFunction + "'");
                }
            } else if (operators.containsKey(token)) {
                if (",".equals(previousToken) || "(".equals(previousToken)) {
                    throw new ExpressionException("Missing parameter(s) for operator " + token +
                            " at character position " + (tokenizer.getPos() - token.length()));
                }
                Operator o1 = operators.get(token);
                CharSequence token2 = stack.isEmpty() ? null : stack.peek();
                while (token2!=null &&
                        operators.containsKey(token2)
                        && ((o1.isLeftAssoc() && o1.getPrecedence() <= operators
                        .get(token2).getPrecedence()) || (o1
                        .getPrecedence() < operators.get(token2)
                        .getPrecedence()))) {
                    outputQueue.add(stack.pop());
                    token2 = stack.isEmpty() ? null : stack.peek();
                }
                stack.push(token);
            } else if ("(".equals(token)) {
                if (previousToken != null) {
                    if (isNumber(previousToken)) {
                        throw new ExpressionException(
                                "Missing operator at character position "
                                        + tokenizer.getPos());
                    }
                    // if the ( is preceded by a valid function, then it
                    // denotes the start of a parameter list
                    if (functions.containsKey(previousToken.toUpperCase(Locale.ROOT))) {
                        outputQueue.add(token);
                    }
                }
                stack.push(token);
            } else if (")".equals(token)) {
                if (operators.containsKey(previousToken)) {
                    throw new ExpressionException("Missing parameter(s) for operator " + previousToken +
                            " at character position " + (tokenizer.getPos() - 1 - previousToken.length()));
                }
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    outputQueue.add(stack.pop());
                }
                if (stack.isEmpty()) {
                    throw new ExpressionException("Mismatched parentheses");
                }
                stack.pop();
                if (!stack.isEmpty()
                        && functions.containsKey(stack.peek().toString().toUpperCase(
                        Locale.ROOT))) {
                    outputQueue.add(stack.pop());
                }
            } else if (Character.isLetter(token.charAt(0))) {
                //myble operatorstring[like|or|and] maybe variable
                outputQueue.add(token); //stack.push(token);
            }
            previousToken = token;
        }
        while (!stack.isEmpty()) {
            CharSequence element = stack.pop();
            if ("(".equals(element) || ")".equals(element)) {
                throw new ExpressionException("Mismatched parentheses");
            }
            if (!operators.containsKey(element)) {
                throw new ExpressionException("Unknown operator or function: "
                        + element);
            }
            outputQueue.add(element);
        }
        ////System.out.println("shuntingYard:"+outputQueue);
        return outputQueue;
    }
    public interface OnVariableListener{
        public Object get(String name);
        public void set(String name, Object value);
    }
    private OnVariableListener variableListener;
    public OnVariableListener getOnVariableListener(){
        return variableListener;
    }
    public Object evaluate(String expression){
        return evaluate(expression, this.variableListener);
    }
    public synchronized Object evaluate(String expression, OnVariableListener variable) {
        if (expression.trim().equalsIgnoreCase("")) {
            return expression;
        }
        this.variableListener = variable;
        expression = quote(expression);
        //System.out.println("expQuote:"+expression);
        //System.out.println("vsargs:"+varstrings);
        Log.i("Expx","expQuote:"+expression);
        Log.i("Expx","vsargs:"+varstrings);

        if (expression.contains("{") && expression.contains("}")) {
            expression = group(expression);
            //System.out.println("vsargs:"+expression);
            Log.i("Expx","vsargs:"+expression);
        }
        return evaluateInternal(expression);
    }
    private Object evaluateInternal(String expression) {
        if (expression.trim().equalsIgnoreCase("")) {
            return expression;
        }

        Stack<LazyNumber> stack = new Stack<LazyNumber>();
        for (CharSequence cs : getRPN(expression)) {
            String token = cs.toString();
            if (operators.containsKey(token)) {
                //System.out.println("o:"+token);
                final LazyNumber v1 = stack.pop();
                final LazyNumber v2 = stack.pop();
                LazyNumber number = new newLazyNumber(token) {
                    public Object eval() {
                        if (getToken().equalsIgnoreCase("=")) {
                            return operators.get(getToken()).eval(v2, v1.eval());
                        }
                        return operators.get(getToken()).eval(v2.eval(), v1.eval());
                    }
                };
                stack.push(number);
            } else if (varstrings.containsKey(token)) {
                //System.out.println("vs:"+token);
                stack.push(new newLazyNumber(token) {
                    public Object eval() {
                        return varstrings.get(getToken());
                    }
                });
            } else if (variables.containsKey(token)||token.startsWith("$")) {
                //System.out.println("v:"+token);
                stack.push(new newLazyNumber(token) {
                    public Object eval() {
                        if (variableListener!=null) {
                            if (isNumber(getToken())) {
                                return getToken();
                            }else{
                                // //System.out.println("vl:"+token);
                                return variableListener.get(getToken());
                            }
                        }
                        //System.out.println("vi:"+getToken());
                        //System.out.println("vi:"+String.valueOf(variables.get(getToken())));
                        return variables.get(getToken());
                    }
                    public String toString() {
                        return getToken(); //need to callback
                    }
                });
            } else if (functions.containsKey(token.toString().toUpperCase(Locale.ROOT))) {
                //System.out.println("f:"+token);
                LazyFunction f = functions.get(token.toString().toUpperCase(Locale.ROOT));
                ArrayList<LazyNumber> p = new ArrayList<LazyNumber>(
                        !f.numParamsVaries() ? f.getNumParams() : 0);

                while (!stack.isEmpty() && stack.peek() != PARAMS_START) {
                    p.add(0, stack.pop());
                }
                if (stack.peek() == PARAMS_START) {
                    stack.pop();
                }
                //exect
                LazyNumber fResult = f.lazyEval(p);
                stack.push(fResult);
            } else if ("(".equals(token)) {
                stack.push(PARAMS_START);
            } else {
                //System.out.println("x:"+token);
                stack.push(new newLazyNumber (token) {
                    public Object eval() {
                        if (variableListener!=null) {
                            if (isNumber(getToken())) {
                                return getToken();
                            }else{
                                //System.out.println("vl:"+token);
                                return variableListener.get(getToken());
                            }
                        }
                        return getToken();//new BigDecimal(token, mc);
                    }
                    public String toString() {
                        return getToken();  //need to callback
                    }
                });
            }
        }
        return stack.pop().eval();//.stripTrailingZeros();
    }
    public interface OnFunctionListener{
        public Object eval(String fname, List<Object> args);
    }

    private Operator addOperator(Operator operator) {
        return operators.put(operator.getOper(), operator);
    }

    private Function addFunction(Function function) {
        return (Function) functions.put(function.getName(), function);
    }

    private LazyFunction addLazyFunction(LazyFunction function) {
        return  functions.put(function.getName(), function);
    }


    public Expression setVariable(String variable, Object value) {
        if (isNumber(String.valueOf(value)))
            variables.put(variable, new Double(String.valueOf(value)));
        else {
            variables.put(variable, value);
            //expression = expression.replaceAll("(?i)\\b" + variable + "\\b", "(" + value + ")");
            //rpn = null;
        }
        return this;
    }
    private List<CharSequence> getRPN(String expression) {
        return validate(shuntingYard(expression));
    }
    private class FunctionString implements CharSequence{
        String value ;
        public FunctionString(String value){
            this.value = value;
        }
        public int length() {
            return value.length();
        }
        public char charAt(int index) {
            return  value.charAt(index);
        }
        public CharSequence subSequence(int start, int end) {
            return value.subSequence(end, end);
        }
        public String toString() {
            return value;
        }
    }

    private List<CharSequence>  validate(List<CharSequence> rpn) {
        Stack<Integer> stack = new Stack<Integer>();

        // push the 'global' scope
        stack.push(0);
        for (final CharSequence cs : rpn) {
            String token = cs.toString();
            if (operators.containsKey(token)) {
                if (stack.peek() < 2) {
                    throw new ExpressionException("Missing parameter(s) for operator " + token);
                }
                // pop the operator's 2 parameters and add the result
                stack.set(stack.size() - 1, stack.peek() - 2 + 1);
            } else if (!(cs instanceof FunctionString) && variables.containsKey(token)||varstrings.containsKey(token)) {
                stack.set(stack.size() - 1, stack.peek() + 1);
            } else if ((cs instanceof FunctionString) && functions.containsKey(token.toString().toUpperCase(Locale.ROOT))) {
                LazyFunction f = functions.get(token.toString().toUpperCase(Locale.ROOT));
                int numParams = stack.pop();
                if (!f.numParamsVaries() && numParams != f.getNumParams()) {
                    throw new ExpressionException("Function " + token + " expected " + f.getNumParams() + " parameters, got " + numParams);
                }
                if (stack.size() <= 0) {
                    throw new ExpressionException("Too many function calls, maximum scope exceeded");
                }
                // push the result of the function
                stack.set(stack.size() - 1, stack.peek() + 1);
            } else if ("(".equals(token)) {
                stack.push(0);
            } else {
                stack.set(stack.size() - 1, stack.peek() + 1);
            }
        }
        if (stack.size() > 1) {
            throw new ExpressionException("Too many unhandled function parameter lists");
        } else if (stack.peek() > 1) {
            throw new ExpressionException("Too many numbers or variables");
        } else if (stack.peek() < 1) {
            throw new ExpressionException("Empty expression");
        }
        return  rpn;
    }

    public Set<String> getDeclaredVariables() {
        return Collections.unmodifiableSet(variables.keySet());
    }
    public Set<String> getDeclaredOperators() {
        return Collections.unmodifiableSet(operators.keySet());
    }
    public Set<String> getDeclaredFunctions() {
        return Collections.unmodifiableSet(functions.keySet());
    }

        /*
	public SmartExpression setPrecision(int precision) {
		this.mc = new MathContext(precision);
		return this;
	}

	public SmartExpression setRoundingMode(RoundingMode roundingMode) {
		this.mc = new MathContext(mc.getPrecision(), roundingMode);
		return this;
	}
        public Iterator<String> getExpressionTokenizer() {
		return new Tokenizer(this.expression);
	}
        public String getExpression() {
		return expression;
	}
        public List<String> getUsedVariables() {
		List<String> result = new ArrayList<String>();
		Tokenizer tokenizer = new Tokenizer(expression);
		while (tokenizer.hasNext()) {
			String token = tokenizer.next();
			if (functions.containsKey(token) || operators.containsKey(token)
					|| token.equals("(") || token.equals(")")
					|| token.equals(",") || isNumber(token)
					|| token.equals("PI") || token.equals("e")
					|| token.equals("TRUE") || token.equals("FALSE")) {
				continue;
			}
			result.add(token);
		}
		return result;
	}
        */





    private static Number getNumber(Object n) {
        if (n instanceof Number) {
            return ((Number)n);
        }else if (isDecimalNumber(String.valueOf(n))){
            return Double.valueOf(String.valueOf(n));
        }
        return 0;
    }
    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
    private static boolean isDecimalNumber(String str) {
        return str.matches("^[-+]?[0-9]*.?[0-9]+([eE][-+]?[0-9]+)?$");
    }
    private static boolean isLongIntegerNumber(String str) {
        return str.matches("-?\\d+");
    }
    private static int getInt(String s) {
        return getNumber(s).intValue();
    }
    private static long getLong(String s) {
        return getNumber(s).longValue();
    }
    private static double getDouble(Object n) {
        return getNumber(n).doubleValue();
    }
    private static String Now() {
        Calendar calendar = Calendar.getInstance();
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }
}
