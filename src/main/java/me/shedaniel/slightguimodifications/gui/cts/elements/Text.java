package me.shedaniel.slightguimodifications.gui.cts.elements;

import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.stream.Stream;

public final class Text {
    private net.minecraft.text.Text text;
    
    private Text(net.minecraft.text.Text text) {
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
    
    public String asFormattedString() {
        return this.text.asFormattedString();
    }
    
    public String asTruncatedString(int length) {
        return this.text.asTruncatedString(length);
    }
    
    public Text copy() {
        return wrap(this.text.copy());
    }
    
    public Text deepCopy() {
        return wrap(this.text.deepCopy());
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
    
    private static Text wrap(net.minecraft.text.Text text) {
        return new Text(text);
    }
}
