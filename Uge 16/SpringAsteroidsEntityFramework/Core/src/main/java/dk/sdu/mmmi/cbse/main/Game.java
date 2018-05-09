package dk.sdu.mmmi.cbse.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.sdu.mmmi.cbse.asteroid.AsteroidPlugin;
import dk.sdu.mmmi.cbse.asteroid.AsteroidProcessor;
import dk.sdu.mmmi.cbse.bullet.BulletSystem;
import dk.sdu.mmmi.cbse.collision.CollisionDetectionSystem;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import dk.sdu.mmmi.cbse.enemy.EnemyPlugin;
import dk.sdu.mmmi.cbse.enemy.EnemyProcessor;
import dk.sdu.mmmi.cbse.managers.GameInputProcessor;
import dk.sdu.mmmi.cbse.playersystem.PlayerPlugin;
import dk.sdu.mmmi.cbse.playersystem.PlayerControlSystem;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Game implements ApplicationListener {

    private static OrthographicCamera cam;
    private ShapeRenderer sr;

    private final GameData gameData = new GameData();
    private List<IEntityProcessingService> entityProcessors = new ArrayList<>();
    private List<IGamePluginService> entityPlugins = new ArrayList<>();
    private IPostEntityProcessingService iPostEntityProcessingService;
    private World world = new World();

    @Override
    public void create() {

        gameData.setDisplayWidth(Gdx.graphics.getWidth());
        gameData.setDisplayHeight(Gdx.graphics.getHeight());

        cam = new OrthographicCamera(gameData.getDisplayWidth(), gameData.getDisplayHeight());
        cam.translate(gameData.getDisplayWidth() / 2, gameData.getDisplayHeight() / 2);
        cam.update();

        sr = new ShapeRenderer();

        Gdx.input.setInputProcessor(new GameInputProcessor(gameData));

        ApplicationContext context = new ClassPathXmlApplicationContext("PlayerBeans.xml", "EnemyBeans.xml", "AsteroidBeans.xml",
                "BulletBeans.xml", "CollisionBeans.xml");
        
        PlayerPlugin playerPlugin = (PlayerPlugin) context.getBean("playerPlugin");
        PlayerControlSystem playerControlSystem = (PlayerControlSystem) context.getBean("playerControlSystem");

        EnemyPlugin enemyPlugin = (EnemyPlugin) context.getBean("enemyPlugin");
        EnemyProcessor enemyProcessor = (EnemyProcessor) context.getBean("enemyProcessor");

        AsteroidPlugin asteroidPlugin = (AsteroidPlugin) context.getBean("asteroidPlugin");
        AsteroidProcessor asteroidProcessor = (AsteroidProcessor) context.getBean("asteroidProcessor");

        BulletSystem bulletSystem = (BulletSystem) context.getBean("bulletSystem");

        iPostEntityProcessingService = (CollisionDetectionSystem) context.getBean("collisionDetectionSystem");

        entityPlugins.add(playerPlugin);
        entityProcessors.add(playerControlSystem);
        entityPlugins.add(enemyPlugin);
        entityProcessors.add(enemyProcessor);
        entityPlugins.add(asteroidPlugin);
        entityProcessors.add(asteroidProcessor);
        entityProcessors.add(bulletSystem);
        // Lookup all Game Plugins using ServiceLoader
        for (IGamePluginService iGamePlugin : entityPlugins) {
            iGamePlugin.start(gameData, world);
        }
    }

    @Override
    public void render() {

        // clear screen to black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameData.setDelta(Gdx.graphics.getDeltaTime());

        update();

        draw();

        gameData.getKeys().update();
    }

    private void update() {
        // Update
        for (IEntityProcessingService entityProcessorService : entityProcessors) {
            entityProcessorService.process(gameData, world);
        }
        iPostEntityProcessingService.process(gameData, world);
    }

    private void draw() {
        for (Entity entity : world.getEntities()) {

            sr.setColor(1, 1, 1, 1);

            sr.begin(ShapeRenderer.ShapeType.Line);

            float[] shapex = entity.getShapeX();
            float[] shapey = entity.getShapeY();

            for (int i = 0, j = shapex.length - 1;
                    i < shapex.length;
                    j = i++) {

                sr.line(shapex[i], shapey[i], shapex[j], shapey[j]);
            }

            sr.end();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
