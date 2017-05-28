package org.jfrog.artifactory.client

import org.hamcrest.CoreMatchers
import org.jfrog.artifactory.client.model.Version
import org.jfrog.artifactory.client.model.repository.settings.impl.RpmRepositorySettingsImpl
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * test that client correctly sends and receives repository configuration with `rpm` package type
 *
 * @author Ivan Vasylivskyi (ivanvas@jfrog.com)
 */
public class RpmPackageTypeRepositoryTests extends BaseRepositoryTests {

    private static String MIN_ARTIFACTORY_VERSION = "5.0.0"
    private boolean rpmPackageSupported

    @BeforeMethod
    protected void setUp() {
        rpmPackageSupported = isRpmPackageSupported()
        settings = new RpmRepositorySettingsImpl()

        settings.with {
            // local
            calculateYumMetadata = rnd.nextBoolean()
            groupFileNames = "groups-${rnd.nextInt()}.xml"
            listRemoteFolderItems = rnd.nextBoolean()

            // remote
            yumRootDepth = rnd.nextInt()
        }

        // only local and remote repository supported
        prepareVirtualRepo = false

        super.setUp()
    }

    private boolean isRpmPackageSupported() {
        Version version = artifactory.system().version()
        return version.isAtLeast(MIN_ARTIFACTORY_VERSION)
    }

    @Test(groups = "rpmPackageTypeRepo")
    public void testRpmLocalRepo() {
        if (!rpmPackageSupported) {
            return
        }
        artifactory.repositories().create(0, localRepo)

        def resp = artifactory.repository(localRepo.getKey()).get()
        resp.getRepositorySettings().with {
            assertThat(packageType, CoreMatchers.is(settings.getPackageType()))

            // local
            assertThat(calculateYumMetadata, CoreMatchers.is(settings.getCalculateYumMetadata()))
            // TODO: property is not returned by the artifactory
            // assertThat(groupFileNames, CoreMatchers.is(specRepo.getGroupFileNames()))
            assertThat(groupFileNames, CoreMatchers.is(CoreMatchers.nullValue()))
            assertThat(yumRootDepth, CoreMatchers.is(settings.getYumRootDepth()))

            // remote
            assertThat(listRemoteFolderItems, CoreMatchers.is(CoreMatchers.nullValue()))
        }
    }

    @Test(groups = "rpmPackageTypeRepo")
    public void testRpmRemoteRepo() {
        if (!rpmPackageSupported) {
            return
        }
        artifactory.repositories().create(0, remoteRepo)

        def resp = artifactory.repository(remoteRepo.getKey()).get()
        resp.getRepositorySettings().with {
            assertThat(packageType, CoreMatchers.is(settings.getPackageType()))

            // remote
            assertThat(listRemoteFolderItems, CoreMatchers.is(settings.getListRemoteFolderItems()))

            // local
            assertThat(calculateYumMetadata, CoreMatchers.is(CoreMatchers.nullValue()))
            assertThat(groupFileNames, CoreMatchers.is(CoreMatchers.nullValue()))
            assertThat(yumRootDepth, CoreMatchers.is(CoreMatchers.nullValue()))
        }
    }

}