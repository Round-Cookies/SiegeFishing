package me.asakura_kukii.siegefishing.argument.tab;

import me.asakura_kukii.siegecore.argument.PArgument;
import me.asakura_kukii.siegecore.argument.PSender;

import java.util.ArrayList;
import java.util.List;

public class TabHandler {
    public static List<String> noTabNext(List<String> sL, PArgument argument) {
        if (argument.nextString() != null) {
            sL.clear();
        }
        return sL;
    }

    public static List<String> onTab(PSender sender, PArgument argument) {
        List<String> sL = new ArrayList<>();

        String s = argument.nextString();
        if (!argument.success) {
            return new ArrayList<>();
        }
        switch (s) {
            default:
                if (PArgument.completeString("info", s)) sL.add("info");
                if (PArgument.completeString("show", s)) sL.add("show");
                if (PArgument.completeString("head", s)) sL.add("head");
                return noTabNext(sL, argument);
        }
    }
}
