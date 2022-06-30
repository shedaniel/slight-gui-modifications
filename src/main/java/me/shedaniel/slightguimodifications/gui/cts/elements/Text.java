package me.shedaniel.slightguimodifications.gui.cts.elements;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Optional;
import java.util.stream.Stream;

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
        StringBuilder stringBuilder = new StringBuilder();
        this.text.getContents().visit((string) -> {
            stringBuilder.append(string);
            return Optional.empty();
        });
        return stringBuilder.toString();
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
        return wrap(Component.literal(s));
    }
    
    public static Text translatable(String s, Object... objects) {
        return wrap(Component.translatable(s, Stream.of(objects).map(o -> {
            if (o instanceof Text)
                return ((Text) o).text;
            return o;
        }).toArray()));
    }
    
    public static Text wrap(MutableComponent text) {
        return new Text(text);
    }
    
    public net.minecraft.network.chat.Component unwrap() {
        return text;
    }
}
