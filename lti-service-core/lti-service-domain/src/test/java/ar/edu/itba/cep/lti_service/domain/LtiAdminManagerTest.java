package ar.edu.itba.cep.lti_service.domain;

import ar.edu.itba.cep.lti_service.models.admin.FrontendDeployment;
import ar.edu.itba.cep.lti_service.models.admin.ToolDeployment;
import ar.edu.itba.cep.lti_service.repositories.FrontendDeploymentRepository;
import ar.edu.itba.cep.lti_service.repositories.ToolDeploymentRepository;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static ar.edu.itba.cep.lti_service.models.admin.FrontendDeployment.EXAM_ID_VARIABLE;
import static ar.edu.itba.cep.lti_service.models.admin.FrontendDeployment.JWT_VARIABLE;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link LtiAdminManager}.
 */
@ExtendWith(MockitoExtension.class)
public class LtiAdminManagerTest {

    /**
     * The {@link ToolDeploymentRepository} that is injected to the {@link LtiAdminManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    private final ToolDeploymentRepository toolDeploymentRepository;
    /**
     * The {@link FrontendDeploymentRepository} that is injected to the {@link LtiAdminManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    private final FrontendDeploymentRepository frontendDeploymentRepository;

    /**
     * The {@link LtiAdminManager} to be tested.
     */
    private final LtiAdminManager ltiAdminManager;


    /**
     * Constructor.
     *
     * @param toolDeploymentRepository     A mocked {@link ToolDeploymentRepository}
     *                                     to be injected into a {@link LtiAdminManager} that will be tested.
     * @param frontendDeploymentRepository A mocked {@link FrontendDeploymentRepository}
     *                                     to be injected into a {@link LtiAdminManager} that will be tested.
     */
    public LtiAdminManagerTest(
            @Mock(name = "toolDeploymentRepository") final ToolDeploymentRepository toolDeploymentRepository,
            @Mock(name = "frontendDeploymentRepository") final FrontendDeploymentRepository frontendDeploymentRepository) {
        this.toolDeploymentRepository = toolDeploymentRepository;
        this.frontendDeploymentRepository = frontendDeploymentRepository;
        this.ltiAdminManager = new LtiAdminManager(toolDeploymentRepository, frontendDeploymentRepository);
    }


    // ================================================================================================================
    // Tool deployments
    // ================================================================================================================

    /**
     * Tests that the searching for all {@link ToolDeployment}s returns the expected {@link List}
     * (i.e the one returned by the {@link ToolDeploymentRepository}).
     *
     * @param toolDeployment1 A {@link ToolDeployment} to be retrieved.
     * @param toolDeployment2 Another {@link ToolDeployment} to be retrieved.
     */
    @Test
    void testFindAll(
            @Mock(name = "toolDeployment1") final ToolDeployment toolDeployment1,
            @Mock(name = "toolDeployment2") final ToolDeployment toolDeployment2) {
        final var toolDeployments = List.of(toolDeployment1, toolDeployment2);
        when(toolDeploymentRepository.findAll()).thenReturn(toolDeployments);
        final var returnedList = ltiAdminManager.getAllToolDeployments();
        Assertions.assertAll(
                "The list returned by manager has issues",
                () -> Assertions.assertTrue(
                        returnedList.containsAll(toolDeployments),
                        "It does not contains all the elements returned by the repository"
                ),
                () -> Assertions.assertEquals(
                        toolDeployments.size(),
                        returnedList.size(),
                        "It does not contain the same amount of elements as the one returned by the repository"
                )
        );
        verify(toolDeploymentRepository, only()).findAll();
        verifyZeroInteractions(frontendDeploymentRepository);
    }

    /**
     * Tests that the searching for all {@link ToolDeployment}s of a given issuer returns the expected {@link List}
     * (i.e the one returned by the {@link ToolDeploymentRepository}).
     *
     * @param toolDeployments A mocked {@link List} of {@link ToolDeployment}
     *                        (the one being returned by the {@link ToolDeploymentRepository}).
     */
    @Test
    void testFindByIssuer(@Mock(name = "toolDeployments") final List<ToolDeployment> toolDeployments) {
        final var issuer = issuer();
        when(toolDeploymentRepository.find(issuer)).thenReturn(toolDeployments);
        Assertions.assertEquals(
                toolDeployments,
                ltiAdminManager.find(issuer),
                "The tool deployments list returned by the manager is not the same as the returned by the repository"
        );
        verify(toolDeploymentRepository, only()).find(issuer);
        verifyZeroInteractions(frontendDeploymentRepository);
    }

    /**
     * Tests that the searching for all {@link ToolDeployment}s of a given issuer and client id
     * returns the expected {@link List} (i.e the one returned by the {@link ToolDeploymentRepository}).
     *
     * @param toolDeployments A mocked {@link List} of {@link ToolDeployment}
     *                        (the one being returned by the {@link ToolDeploymentRepository}).
     */
    @Test
    void testFindByIssuerAndClientId(@Mock(name = "toolDeployments") final List<ToolDeployment> toolDeployments) {
        final var issuer = issuer();
        final var clientId = clientId();
        when(toolDeploymentRepository.find(clientId, issuer)).thenReturn(toolDeployments);
        Assertions.assertEquals(
                toolDeployments,
                ltiAdminManager.find(clientId, issuer),
                "The tool deployments list returned by the manager is not the same as the returned by the repository"
        );
        verify(toolDeploymentRepository, only()).find(clientId, issuer);
        verifyZeroInteractions(frontendDeploymentRepository);
    }

    /**
     * Tests that the searching for a {@link ToolDeployment} by its id
     * returns the expected {@link ToolDeployment} (i.e the one returned by the {@link ToolDeploymentRepository}).
     *
     * @param toolDeployment A mocked  {@link ToolDeployment}
     *                       (the one being returned by the {@link ToolDeploymentRepository}).
     */
    @Test
    void testGetToolDeploymentById(@Mock(name = "toolDeployment") final ToolDeployment toolDeployment) {
        final var id = toolDeploymentId();
        when(toolDeploymentRepository.findById(id)).thenReturn(Optional.of(toolDeployment));
        Assertions.assertEquals(
                Optional.of(toolDeployment),
                ltiAdminManager.getToolDeploymentById(id),
                "The tool deployment returned by the manager is not the same as the returned by the repository"
        );
        verify(toolDeploymentRepository, only()).findById(id);
        verifyZeroInteractions(frontendDeploymentRepository);
    }

    /**
     * Tests that the searching for a {@link ToolDeployment} by a given issuer, client id and deployment id
     * returns the expected {@link ToolDeployment} (i.e the one returned by the {@link ToolDeploymentRepository}).
     *
     * @param toolDeployment A mocked  {@link ToolDeployment}
     *                       (the one being returned by the {@link ToolDeploymentRepository}).
     */
    @Test
    void testFindByIssuerClientIdAndDeploymentId(@Mock(name = "toolDeployment") final ToolDeployment toolDeployment) {
        final var issuer = issuer();
        final var clientId = clientId();
        final var deploymentId = deploymentId();
        when(toolDeploymentRepository.find(deploymentId, clientId, issuer)).thenReturn(Optional.of(toolDeployment));
        Assertions.assertEquals(
                Optional.of(toolDeployment),
                ltiAdminManager.find(deploymentId, clientId, issuer),
                "The tool deployment returned by the manager is not the same as the returned by the repository"
        );
        verify(toolDeploymentRepository, only()).find(deploymentId, clientId, issuer);
        verifyZeroInteractions(frontendDeploymentRepository);
    }

    /**
     * Tests that registering a {@link ToolDeployment} works as expected when no one exists for a given
     * issuer, client id and deployment id (i.e the {@link ToolDeployment} is created, saved and returned).
     */
    @Test
    void testToolDeploymentRegistration() {
        final var deploymentId = deploymentId();
        final var clientId = clientId();
        final var issuer = issuer();
        final var oidcAuthenticationEndpoint = oidcAuthenticationEndpoint();
        final var jwksEndpoint = jwksEndpoint();
        when(toolDeploymentRepository.exists(deploymentId, clientId, issuer)).thenReturn(false);
        when(toolDeploymentRepository.save(any(ToolDeployment.class))).then(i -> i.getArgument(0));

        final var toolDeployment = ltiAdminManager.registerToolDeployment(
                deploymentId,
                clientId,
                issuer,
                oidcAuthenticationEndpoint,
                jwksEndpoint
        );

        Assertions.assertAll(
                "Creating a ToolDeployment is not working as expected",
                () -> Assertions.assertEquals(deploymentId, toolDeployment.getDeploymentId(), "Deployment id does not match"),
                () -> Assertions.assertEquals(clientId, toolDeployment.getClientId(), "Client id does not match"),
                () -> Assertions.assertEquals(issuer, toolDeployment.getIssuer(), "Issuer does not match"),
                () -> Assertions.assertEquals(
                        oidcAuthenticationEndpoint,
                        toolDeployment.getOidcAuthenticationEndpoint(),
                        "The Open-Id Connect authentication endpoint does not match"
                ),
                () -> Assertions.assertEquals(
                        jwksEndpoint,
                        toolDeployment.getJwksEndpoint(),
                        "The JWKS endpoint does not match"
                )
        );

        verify(toolDeploymentRepository, times(1)).exists(deploymentId, clientId, issuer);
        verify(toolDeploymentRepository, times(1)).save(
                argThat(
                        td -> deploymentId.equals(td.getDeploymentId())
                                && clientId.equals(td.getClientId())
                                && issuer.equals(td.getIssuer())
                                && oidcAuthenticationEndpoint.equals(td.getOidcAuthenticationEndpoint())
                                && jwksEndpoint.equals(td.getJwksEndpoint())
                )
        );
        verifyNoMoreInteractions(toolDeploymentRepository);
        verifyZeroInteractions(frontendDeploymentRepository);
    }

    /**
     * Tests that registering a {@link ToolDeployment} works as expected when already exists one for a given
     * issuer, client id and deployment id (i.e an {@link UniqueViolationException} is thrown).
     */
    @Test
    void testToolDeploymentRegistrationUniqueness() {
        final var deploymentId = deploymentId();
        final var clientId = clientId();
        final var issuer = issuer();
        when(toolDeploymentRepository.exists(deploymentId, clientId, issuer)).thenReturn(true);

        Assertions.assertThrows(
                UniqueViolationException.class,
                () -> ltiAdminManager.registerToolDeployment(
                        deploymentId,
                        clientId,
                        issuer,
                        oidcAuthenticationEndpoint(),
                        jwksEndpoint()
                ),
                "Registration of a Tool Deployment with a given deployment id, client id and issuer" +
                        " that already exists is being allowed"
        );
        verify(toolDeploymentRepository, only()).exists(deploymentId, clientId, issuer);
        verifyZeroInteractions(frontendDeploymentRepository);
    }

    /**
     * Tests that unregistering a {@link ToolDeployment} by its id (when there is such) works as expected
     * (it is effectively deleted).
     */
    @Test
    void testToolDeploymentUnregistrationForExistingToolDeployment() {
        final var id = toolDeploymentId();
        when(toolDeploymentRepository.existsById(id)).thenReturn(true);
        doNothing().when(toolDeploymentRepository).deleteById(id);

        ltiAdminManager.unregisterToolDeployment(id);

        verify(toolDeploymentRepository, times(1)).existsById(id);
        verify(toolDeploymentRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(toolDeploymentRepository);
        verifyZeroInteractions(frontendDeploymentRepository);
    }

    /**
     * Tests that unregistering a {@link ToolDeployment} by its id (when there is not such) works as expected
     * (nothing happens).
     */
    @Test
    void testToolDeploymentUnregistrationForNonExistingToolDeployment() {
        final var id = toolDeploymentId();
        when(toolDeploymentRepository.existsById(id)).thenReturn(false);

        ltiAdminManager.unregisterToolDeployment(id);

        verify(toolDeploymentRepository, only()).existsById(id);
        verifyZeroInteractions(frontendDeploymentRepository);
    }


    // ================================================================================================================
    // Frontend deployment
    // ================================================================================================================

    /**
     * Tests that registering the {@link FrontendDeployment} works as expected when no one exists
     * (i.e the {@link FrontendDeployment} is created, saved and returned).
     */
    @Test
    void testFrontendDeploymentRegistration() {
        final var examCreationUrl = examCreationUrl();
        final var examTakingUrlTemplate = examTakingUrlTemplate();

        when(frontendDeploymentRepository.count()).thenReturn(0L);
        when(frontendDeploymentRepository.save(any(FrontendDeployment.class))).then(i -> i.getArgument(0));

        final var frontendDeployment = ltiAdminManager.registerFrontend(
                examCreationUrl,
                examTakingUrlTemplate
        );

        Assertions.assertAll(
                "Creating a FrontendDeployment is not working as expected",
                () -> Assertions.assertEquals(
                        examCreationUrl,
                        frontendDeployment.getExamCreationUrl(),
                        "The Exam Creation url does not match"
                ),
                () -> Assertions.assertEquals(
                        examTakingUrlTemplate,
                        frontendDeployment.getExamTakingUrlTemplate(),
                        "The Exam Taking url template does not match"
                )
        );

        verifyZeroInteractions(toolDeploymentRepository);
        verify(frontendDeploymentRepository, times(1)).count();
        verify(frontendDeploymentRepository, times(1)).save(
                argThat(
                        fd -> examCreationUrl.equals(fd.getExamCreationUrl())
                                && examTakingUrlTemplate.equals(fd.getExamTakingUrlTemplate())
                )
        );
        verifyNoMoreInteractions(frontendDeploymentRepository);
    }

    /**
     * Tests that registering the {@link FrontendDeployment} works as expected when already exists one
     * (i.e an {@link UniqueViolationException} is thrown).
     */
    @Test
    void testFrontendDeploymentRegistrationUniqueness() {
        when(frontendDeploymentRepository.count()).thenReturn((long) new Random().nextInt(Short.MAX_VALUE));

        Assertions.assertThrows(
                UniqueViolationException.class,
                () -> ltiAdminManager.registerFrontend(
                        examCreationUrl(),
                        examTakingUrlTemplate()
                ),
                "Registration of a Frontend Deployment (when there is already another frontend registered)" +
                        " is being allowed"
        );

        verifyZeroInteractions(toolDeploymentRepository);
        verify(frontendDeploymentRepository, only()).count();
    }

    /**
     * Tests that unregistering the {@link FrontendDeployment} works as expected
     * (always the {@link FrontendDeploymentRepository#deleteAll()} is called).
     */
    @Test
    void testFrontendDeploymentUnregistration() {
        doNothing().when(frontendDeploymentRepository).deleteAll();

        ltiAdminManager.unregisterFrontend();

        verifyZeroInteractions(toolDeploymentRepository);
        verify(frontendDeploymentRepository, only()).deleteAll();
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    // ========================================
    // Tool Deployment
    // ========================================

    /**
     * @return A valid {@link ToolDeployment} id.
     */
    private static UUID toolDeploymentId() {
        return UUID.randomUUID();
    }

    /**
     * @return A valid deployment id.
     */
    private static String deploymentId() {
        return UUID.randomUUID().toString();
    }

    /**
     * @return A valid client id.
     */
    private static String clientId() {
        return UUID.randomUUID().toString();
    }

    /**
     * @return A valid issuer.
     */
    private static String issuer() {
        return "https://" + Faker.instance().internet().domainName();
    }

    /**
     * @return A valid Open-Id Connect authentication endpoint
     */
    private static String oidcAuthenticationEndpoint() {
        return "https://" + Faker.instance().internet().domainName() + "/oidc";
    }

    /**
     * @return A valid JWKS endpoint
     */
    private static String jwksEndpoint() {
        return "https://" + Faker.instance().internet().domainName() + "/jwks";
    }


    // ========================================
    // Frontend Deployment
    // ========================================

    /**
     * @return A valid {@link FrontendDeployment} id.
     */
    private static UUID frontendDeploymentId() {
        return UUID.randomUUID();
    }

    /**
     * @return A valid "exam creation" url.
     */
    private static String examCreationUrl() {
        return "https://" + Faker.instance().internet().domainName() + "/create-exam";
    }

    /**
     * @return A valid "exam taking" url template.
     */
    private static String examTakingUrlTemplate() {
        return "https://"
                + Faker.instance().internet().domainName()
                + "/take-exam/"
                + EXAM_ID_VARIABLE
                + "?token="
                + JWT_VARIABLE
                ;
    }
}
