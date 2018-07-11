package bitbucketpullrequestbuilder.bitbucketpullrequestbuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.EnvVars;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

class BuildResultLoader {
    private static final Logger logger = Logger.getLogger(BitbucketBuildListener.class.getName());
    private BuildResult buildResult;
    private static final String resultFileName = "ci_bitbucket_comment.json";

    BuildResultLoader(final EnvVars vars) {
        buildResult = new BuildResult();
        if (vars != null) {
            ObjectMapper objectMapper = new ObjectMapper();

            String PrBuilderFilePath = vars.get("WORKSPACE") + File.separator + resultFileName;

            logger.fine("BitbucketBuildListener BuildResult loading from: " + PrBuilderFilePath);
            try {
                buildResult = objectMapper.readValue(new File(PrBuilderFilePath), BuildResult.class);
            } catch (IOException e) {
                logger.fine("BitbucketBuildListener BuildResult was ignored.");
            }
        }
    }

    public BuildResult getBuildResult() {
        return buildResult;
    }
}
