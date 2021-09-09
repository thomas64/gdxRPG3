package nl.t64.cot;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;


public class GameTest {

    private static Application application;

    @BeforeAll
    public static void init() {
        application = new HeadlessApplication(new GdxRpg3() {
            //@formatter:off
            @Override public void create() {}
            @Override public void resize(int width, int height) {}
            @Override public void render() {}
            @Override public void pause() {}
            @Override public void resume() {}
            @Override public void dispose() {}
            //@formatter:on
        });
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
    }

    @AfterAll
    public static void cleanUp() {
        application.exit();
        application = null;
    }

}
