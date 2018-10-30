package io.jenkins.plugins.build;

import hudson.Extension;
import hudson.Util;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DeleteBuildLibsAction {

    private static final Logger LOGGER = Logger.getLogger(DeleteBuildLibsAction.class.getName());
    /**
     * Listener.
     */
    @Extension
    public static class Listener extends RunListener<Run<?, ?>> {

        @Override
        public void onCompleted(Run<?, ?> build, TaskListener listener) {
            String subFolder = "libs";
            File rootDir = new File(build.getRootDir(), subFolder);
            try {
                if (rootDir.isDirectory()) {
                    File tmp = new File(rootDir.getParentFile(), "." + subFolder);
                    if (tmp.exists()) {
                        Util.deleteRecursive(tmp);
                    }

                    boolean renamingSucceeded = rootDir.renameTo(tmp);
                    Util.deleteRecursive(tmp);
                    if (tmp.exists()) {
                        tmp.deleteOnExit();
                    }

                    if (!renamingSucceeded) {
                        throw new IOException(rootDir + " is in use");
                    } else {
                        LOGGER.log(Level.INFO, "{0}: {1} successfully deleted", new Object[]{this, rootDir});
                    }
                }
            }catch(IOException e){
                LOGGER.log(Level.SEVERE, "{0}: IO exception. {1}", new Object[]{this, e});
            }


            super.onCompleted(build, listener);
        }
    } // end Listener

}
