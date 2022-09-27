package ide;

import java.util.regex.Pattern;

public class Terminal {

    private final String name;
    private final Pattern pattern;
    private final int priority;

    public Terminal(String n, String pat){

        name = n;
        pattern = Pattern.compile(pat);
        priority = 0;

    }

    public Terminal(String n, String pat, int pr){

        name = n;
        pattern = Pattern.compile(pat);
        priority = pr;

    }

    public String getName(){
        return name;
    }

    public int getPriority(){
        return priority;
    }


    public boolean matches(CharSequence charSequence) {
        return pattern.matcher(charSequence).matches();
    }

}