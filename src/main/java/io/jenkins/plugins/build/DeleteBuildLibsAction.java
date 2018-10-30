package io.jenkins.plugins.build;

import hudson.Extension;
import hudson.Util;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

import java.io.File;
import java.io.IOException;


public class DeleteBuildLibsAction {

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
                        listener.getLogger().println(this.getClass().getName() + " : " + rootDir.toString() + " is successfully deleted");
                    }
                }
            } catch (IOException e) {
                listener.getLogger().println(this.getClass().getName() + " : " + rootDir.toString() + " cannot be deleted : " + e.getMessage());
            }


            super.onCompleted(build, listener);
        }
    } // end Listener

}
