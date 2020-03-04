package com.geekbrains.rpg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Hero {
    //private Projectile projectile;
    private TextureRegion texture;
    private TextureRegion texturePointer;
    private TextureRegion textureHp;
    private Vector2 position;
    private Vector2 dst;
    private Vector2 tmp;
    private float lifetime;
    private float speed;
    private int hp;
    private int hpMax;
    private StringBuilder strBuilder;

    public Hero(TextureAtlas atlas) {
        this.texture = atlas.findRegion("knight");
        this.texturePointer = atlas.findRegion("pointer");
        this.textureHp = atlas.findRegion("hp");
        this.position = new Vector2(100, 100);

        //this.projectile = new Projectile(atlas); //при создании пробрасываем в снаряд атасс
        this.dst = new Vector2(position);
        this.tmp = new Vector2(0, 0);
        this.speed = 300.0f;
        this.hpMax = 10;
        this.hp = 10;
        this.strBuilder = new StringBuilder();
    }

    public void render(SpriteBatch batch) {

        batch.draw(texturePointer, dst.x - 30, dst.y - 30, 30, 30, 60, 60, 0.5f, 0.5f, lifetime * 90.0f);
        batch.draw(texture, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, 0);
        batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);

    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.setLength(0); //отчистить
        strBuilder.append("Class: ").append("Knight").append("\n"); //не использовать конкатенацию только аппенд
        strBuilder.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        font.draw(batch, strBuilder, 10, 710);//нарисуй себя на баче
    }

    public void update(float dt, GeekRpgGame game) {
        //projectile.update(dt, target); //неправильно, они должны жить отдельно от героев
        lifetime += dt;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) { //если левая кнопка только нажата туда мы пошли
            dst.set(Gdx.input.getX(), 720.0f - Gdx.input.getY());
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) { //если нажата правая кнопка мы туда пульнули
            for (Projectile p : game.getPull()) {
                if(!p.isActive()){
                    p.setup(position.x, position.y, Gdx.input.getX(), 720.0f - Gdx.input.getY());
                    break;
                }
            }
            //projectile.setup(position.x, position.y, Gdx.input.getX(), 720.0f - Gdx.input.getY());
            //запустить инициализацию у сетапа передав в его позицию и координаты направления
        }

        tmp.set(dst).sub(position).nor().scl(speed); //tmp = вектор из position в dst длинною в 100
        //tmp.set(dst) - в темп записываем то что было в дст(ДСТ становится ТМП)
        //(dst).sub(position) - получить вектор в сторону назначения (из точки назначениея вычитаем точку назаначения)
        //(dst).sub(position).nor() - нормируем этот вектор, получаем направлление
        //(dst).sub(position).nor().scl(speed) - масштабируем на скорость, получаем движение в сторону цели
        //на каждом кадре двигаемся к цели
        if (position.dst(dst) > speed * dt) {
            //(если нам нужно пройти большее растояние че мы можем пройти за 1 кадр- туда бежим)
            // если расстояние между персонажем и тем куда ему нужно меньше чем скорость * ДТ то тогда мы шевелимся
            position.mulAdd(tmp, dt);
            //вычисления в темпе чтоб не перегружать железку
            //position.mulAdd(tmp, dt) - к вектору position добавляем вектор движения
            //прибавление с умножением на ДТ обоих координат в нужном направлении
        } else {
            position.set(dst);//иначе сразу перемещаюсь в точку куда мне нужно
        }
    }
}