package me.shedaniel.slightguimodifications.gui.cts.elements;

import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public final class Text {
    private MutableComponent text;
    
    private Text(MutableComponent text) {
        this.text = text;
    }
    
    public Text append(String text) {
        return this.append(literal(text));
    }
    
    public Text append(Text text) {
        return wrap(this.text.append(text.text));
    }
    
    public String asString() {
        return this.text.getContents();
    }
    
    public String getString() {
        return this.text.getString();
    }
    
    public String asTruncatedString(int length) {
        return this.text.getString(length);
    }
    
    public Text copy() {
        return wrap(this.text.plainCopy());
    }
    
    public Text shallowCopy() {
        return wrap(this.text.copy());
    }
    
    public Text formatted(String... s) {
        return wrap(this.text.withStyle(Stream.of(s).map(ChatFormatting::getByName).toArray(ChatFormatting[]::new)));
    }
    
    public static Text literal(String s) {
        return wrap(new TextComponent(s));
    }
    
    public static Text translatable(String s, Object... objects) {
        return wrap(new TranslatableComponent(s, Stream.of(objects).map(o -> {
            if (o instanceof Text)
                return ((Text) o).text;
            return o;
        }).toArray()));
    }
    
    private static Text wrap(MutableComponent text) {
        return new Text(text);
    }
    
    public net.minecraft.network.chat.Component unwrap() {
        return text;
    }
}
