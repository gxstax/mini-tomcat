package test;

import com.mini.ContainerEvent;
import com.mini.ContainerListener;

/**
 * <p>
 *
 * </p>
 *
 * @author Ant
 * @since 2024/3/26 17:19
 */
public class TestListener implements ContainerListener {

    @Override
    public void containerEvent(ContainerEvent event) {
        System.out.println(event);
    }
}
