package me.shedaniel.slightguimodifications.gui.cts.elements;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.stream.Stream;

public final class Text {
    private MutableText text;
    
    private Text(MutableText text) {
        this.text = text;
    }
    
    public Text append(String text) {
        return this.append(literal(text));
    }
    
    public Text append(Text text) {
        return wrap(this.text.append(text.text));
    }
    
    public String asString() {
        return this.text.asString();
    }
    
    public String getString() {
        return this.text.getString();
    }
    
    public String asTruncatedString(int length) {
        return this.text.asTruncatedString(length);
    }
    
    public Text copy() {
        return wrap(this.text.copy());
    }
    
    public Text shallowCopy() {
        return wrap(this.text.shallowCopy());
    }
    
    public Text formatted(String... s) {
        return wrap(this.text.formatted(Stream.of(s).map(Formatting::byName).toArray(Formatting[]::new)));
    }
    
    public static Text literal(String s) {
        return wrap(new LiteralText(s));
    }
    
    public static Text translatable(String s, Object... objects) {
        return wrap(new TranslatableText(s, Stream.of(objects).map(o -> {
            if (o instanceof Text)
                return ((Text) o).text;
            return o;
        }).toArray()));
    }
    
    private static Text wrap(MutableText text) {
        return new Text(text);
    }
    
    public net.minecraft.text.Text unwrap() {
        return text;
    }
}
