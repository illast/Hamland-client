package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;

public class Loot extends GameObject {

    private int type;

    /**
     * Constructor for all objects on the screen.
     *
     * @param texture object texture.
     * @param x       X-coordinate.
     * @param y       Y-coordinate.
     * @param width   object width.
     * @param height  object height.
     */
    public Loot(Texture texture, float x, float y, float width, float height) {
        super(texture, x, y, width, height);
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
