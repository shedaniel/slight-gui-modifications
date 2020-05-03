package me.shedaniel.slightguimodifications.gui;

import net.minecraft.client.font.Font;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Matrix4f;

import java.util.List;

public class WrappedTextRenderer extends TextRenderer {
    private final TextRenderer textRenderer;
    
    public WrappedTextRenderer(TextRenderer textRenderer) {
        super(null, null);
        this.textRenderer = textRenderer;
    }
    
    @Override
    public void setFonts(List<Font> fonts) {textRenderer.setFonts(fonts);}
    
    @Override
    public void close() {textRenderer.close();}
    
    @Override
    public int drawWithShadow(String text, float x, float y, int color) {return textRenderer.drawWithShadow(text, x, y, color);}
    
    @Override
    public int draw(String text, float x, float y, int color) {return textRenderer.draw(text, x, y, color);}
    
    @Override
    public String mirror(String text) {return textRenderer.mirror(text);}
    
    @Override
    public int draw(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int backgroundColor, int light) {return textRenderer.draw(text, x, y, color, shadow, matrix, vertexConsumerProvider, seeThrough, backgroundColor, light);}
    
    @Override
    public int getStringWidth(String text) {return textRenderer.getStringWidth(text);}
    
    @Override
    public float getCharWidth(char character) {return textRenderer.getCharWidth(character);}
    
    @Override
    public String trimToWidth(String text, int width) {return textRenderer.trimToWidth(text, width);}
    
    @Override
    public String trimToWidth(String text, int width, boolean rightToLeft) {return textRenderer.trimToWidth(text, width, rightToLeft);}
    
    @Override
    public void drawTrimmed(String text, int x, int y, int maxWidth, int color) {textRenderer.drawTrimmed(text, x, y, maxWidth, color);}
    
    @Override
    public int getStringBoundedHeight(String text, int maxWidth) {return textRenderer.getStringBoundedHeight(text, maxWidth);}
    
    @Override
    public void setRightToLeft(boolean rightToLeft) {textRenderer.setRightToLeft(rightToLeft);}
    
    @Override
    public List<String> wrapStringToWidthAsList(String text, int width) {return textRenderer.wrapStringToWidthAsList(text, width);}
    
    @Override
    public String wrapStringToWidth(String text, int width) {return textRenderer.wrapStringToWidth(text, width);}
    
    @Override
    public int getCharacterCountForWidth(String text, int offset) {return textRenderer.getCharacterCountForWidth(text, offset);}
    
    @Override
    public int findWordEdge(String text, int direction, int position, boolean skipWhitespaceToRightOfWord) {return textRenderer.findWordEdge(text, direction, position, skipWhitespaceToRightOfWord);}
    
    @Override
    public boolean isRightToLeft() {return textRenderer.isRightToLeft();}
}
