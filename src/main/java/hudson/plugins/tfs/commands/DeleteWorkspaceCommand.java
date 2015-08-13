package hudson.plugins.tfs.commands;

import com.microsoft.tfs.core.clients.versioncontrol.VersionControlConstants;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Workspace;
import hudson.model.TaskListener;
import hudson.plugins.tfs.model.MockableVersionControlClient;
import hudson.plugins.tfs.model.Server;

import java.io.PrintStream;
import java.util.concurrent.Callable;

public class DeleteWorkspaceCommand extends AbstractCallableCommand {

    private static final String DeletingTemplate = "Deleting workspace '%s' owned by '%s'...";
    private static final String DeletedTemplate = "Deleted workspace '%s'.";

    private final String workspaceName;

    public DeleteWorkspaceCommand(final Server server, final String workspaceName) {
        super(server);
        this.workspaceName = workspaceName;
    }

    public Callable<Void> getCallable() {
        return new Callable<Void>() {
            public Void call() {
                final Server server = getServer();
                final MockableVersionControlClient vcc = server.getVersionControlClient();
                final TaskListener listener = server.getListener();
                final PrintStream logger = listener.getLogger();
                final String userName = server.getUserName();

                final String deletingMessage = String.format(DeletingTemplate, workspaceName, userName);
                logger.println(deletingMessage);

                final Workspace innerWorkspace = vcc.queryWorkspace(workspaceName, VersionControlConstants.AUTHENTICATED_USER);
                vcc.deleteWorkspace(innerWorkspace);

                final String deletedMessage = String.format(DeletedTemplate, workspaceName);
                logger.println(deletedMessage);

                return null;
            }
        };
    }
}
