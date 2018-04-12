package dk.sdu.mmmi.cbse.bullet;

import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public void start(BundleContext context) throws Exception {
        context.registerService(IEntityProcessingService.class, new BulletSystem(), null);
    }

    public void stop(BundleContext context) throws Exception {
    }

}
