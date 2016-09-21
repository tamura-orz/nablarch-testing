package nablarch.test.core.standalone;

import nablarch.fw.ExecutionContext;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.launcher.Main;
import nablarch.test.RepositoryInitializer;

/**
 * リクエスト単体テスト用のメインクラス。<br/>
 *
 * @author T.Kawasaki
 */
public class MainForRequestTesting extends Main {

    /**
     * {@inheritDoc}
     * テスト対象実行後にリポジトリの再初期化を行う。
     */
    @Override
    public Integer handle(CommandLine commandLine, ExecutionContext context) {
        try {
            return super.handle(commandLine, context);
        } finally {
            RepositoryInitializer.revertDefaultRepository();
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void setUpSystemRepository(String configFilePath) {
        RepositoryInitializer.reInitializeRepository(configFilePath);
    }
}
