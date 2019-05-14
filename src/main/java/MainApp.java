import com.google.inject.*;
import controller.UserController;
import controller.UserModule;
import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.*;

public class MainApp {

    private static UserController userController;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new UserModule());
        userController = injector.getInstance(UserController.class);
        Javalin app = Javalin.create().start(7000);
        app.routes(() -> {
            path("user", () -> {
                post(ctx-> userController.postHandle(ctx));
            });
        });
        app.error(400,(ctx)->ctx.json("bad request") );
    }

}