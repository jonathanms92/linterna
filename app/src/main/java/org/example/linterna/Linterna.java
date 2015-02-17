package org.example.linterna;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

/**
 * Created by jonathan on 9/02/15.
 */
public class Linterna {

    private Camera camera;

    private Parameters parameters;

    private boolean on;

    public Linterna()
    {
        camera = Camera.open();
        parameters = camera.getParameters();
        camera.startPreview();
        on = false;
    }

    public void on()
    {
        if (!on)
        {
            on = true;
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
        }
    }

    public void off()
    {
        if (on)
        {
            on = false;
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
        }
    }

    public void release()
    {
        camera.stopPreview();
        camera.release();
    }
    public boolean isOn()
    {
        // Opcion alternativa
        // return (parameters.getFlashMode() == Parameters.FLASH_MODE_TORCH);
        return on;
    }
}
