package com.redsponge.nonviolent.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.physics.PhysicsWorld;

public class EnemyPaper extends Enemy {

    private Rectangle attackRectangle;
    private float timeUntilAttack;
    private boolean inAttack;
    private int range;
    private float timeSinceAttack;

    private Vector2 vel;
    private Vector2 self;
    private Vector2 playerVec;
    private float timeAlive;

    public EnemyPaper(PhysicsWorld worldIn, Player player, int x, int y, float speed, int range) {
        super(worldIn, player, x, y, speed);
        this.range = range;
        attackRectangle = null;
        timeUntilAttack = 0;
        timeSinceAttack = 0;
        vel = new Vector2();
        self = new Vector2();
        playerVec = new Vector2();

        size.set(30, 30);
        timeAlive = 0;
    }

    @Override
    public void update(float delta) {
        timeAlive += delta;
        self.set(pos.x, pos.y);
        playerVec.set(player.pos.x, player.pos.y);

        if(inRange() && !inAttack) {
            timeUntilAttack -= delta;
            System.out.println(timeUntilAttack);
            if(timeUntilAttack <= 0) {
                startAttack();
            }
        }
        if(inAttack) {
            timeSinceAttack += delta;
            if(timeSinceAttack >= 0.5f && attackRectangle == null) {
                spawnAttack();
            }
            if(timeSinceAttack >= 0.7f) {
                stopAttack();
            }
        } else {
            AIActions.follow(self, playerVec, vel, 1, 0);
            moveX(vel.x * delta * speed, null);
            moveY(vel.y * delta * speed, null);
        }

        tryKill(MoveType.ROCK);
    }

    private void spawnAttack() {
        attackRectangle = new Rectangle(pos.x - range + size.x / 2, pos.y - range + size.y / 2, range * 2, range * 2);
    }

    private void stopAttack() {
        inAttack = false;
        attackRectangle = null;
    }

    private void startAttack() {
        inAttack = true;
        timeSinceAttack = 0;
        timeUntilAttack = 5;
        System.out.println("ATTACK!");
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = GameScreen.paperAnimation.getKeyFrame(timeAlive);
        float w = frame.getRegionWidth() * 2;
        float h = frame.getRegionHeight() * 2;
        batch.draw(frame, pos.x - w / 2 + size.x / 2f, (float) (pos.y - h / 2 + size.y / 2f + Math.sin(timeAlive * 5) * 20), w, h);
    }

    private boolean inRange() {
        return Vector2.dst2(player.pos.x, player.pos.y, pos.x, pos.y) < range * range;
    }

    @Override
    public Rectangle getAttackRectangle() {
        return attackRectangle;
    }
}
