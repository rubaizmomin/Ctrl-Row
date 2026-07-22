package com.rubaizmomin.CsvSuiteRunner.actions;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

public class HelloWorldAction extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        boolean visible = false;

        if (psiFile instanceof XmlFile xmlFile) {

            if (xmlFile.getRootTag() != null) {

                visible =
                        "suite".equals(xmlFile.getRootTag().getName());

            }
        }

        e.getPresentation().setVisible(visible);
        e.getPresentation().setEnabled(visible);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();

        VirtualFile testngFile =
                e.getData(CommonDataKeys.VIRTUAL_FILE);


        if (project == null || testngFile == null) {
            return;
        }


        CsvRowDialog dialog = new CsvRowDialog();


        if (!dialog.showAndGet()) {
            return;
        }


        String rows = dialog.getRows();


        if (rows.isEmpty()) {
            return;
        }


        runTestNG(
                project,
                testngFile,
                rows
        );
    }

    private String findMaven() {

        String mavenHome = System.getenv("MAVEN_HOME");

        if (mavenHome != null) {
            return mavenHome + "\\bin\\mvn.cmd";
        }

        return "mvn.cmd";
    }

    private void runTestNG(
            Project project,
            VirtualFile testngFile,
            String rows
    ) {


        String testngPath =
                testngFile.getPath();


        GeneralCommandLine command =
                new GeneralCommandLine();


        command.setExePath(findMaven());


        command.addParameters(
                "test"
        );


        command.addParameters(
                "-Dtestng.xml=" + testngPath
        );


        command.addParameters(
                "-Dcsv.rows=" + rows
        );

        command.setWorkDirectory(
                project.getBasePath()
        );

        try {

            OSProcessHandler processHandler = createProcessHandler(command);

            processHandler.startNotify();

        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
    }

    private static @NotNull OSProcessHandler createProcessHandler(GeneralCommandLine command) throws ExecutionException {
        OSProcessHandler processHandler =
                new OSProcessHandler(command);

        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void onTextAvailable(
                    @NotNull ProcessEvent event,
                    @NotNull Key outputType
            ) {
                System.out.print(event.getText());
            }

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                System.out.println(
                        "Process finished with exit code: "
                                + event.getExitCode()
                );
            }
        });
        return processHandler;
    }
}