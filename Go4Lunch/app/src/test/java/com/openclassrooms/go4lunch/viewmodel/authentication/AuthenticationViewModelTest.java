package com.openclassrooms.go4lunch.viewmodel.authentication;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.AuthCredential;
import com.openclassrooms.go4lunch.data.local.FakeUserRepository;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.UserFactory;
import com.openclassrooms.go4lunch.utilities.RxImmediateSchedulerRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static com.openclassrooms.go4lunch.utilities.LiveDataTestUtil.getValue;

public class AuthenticationViewModelTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    private AuthenticationViewModel authenticationViewModel;

    private FakeUserRepository userRepository;

    @Before
    public void setUpViewModel() throws Exception {

        userRepository = new FakeUserRepository();
        authenticationViewModel = new AuthenticationViewModel(userRepository);
    }

    @After
    public void tearDown() throws Exception {
        userRepository = null;
        authenticationViewModel = null;
    }

    @Test
    public void sign_in_with_credential() throws InterruptedException {
        AuthCredential authCredential = Mockito.mock(AuthCredential.class);
        authenticationViewModel.signInWithCredential(authCredential);

        User userAuthenticated = (User) getValue(authenticationViewModel.userAuthenticated());

        assertThat(userAuthenticated).isNotNull();
        assertThat(userAuthenticated.isAuthenticated()).isTrue();
    }

    @Test
    public void sign_in_with_email_and_password() throws InterruptedException {
        String email = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();

        User user = UserFactory.makeUser();
        user.setEmail(email);

        userRepository.saveUser(user).blockingGet();
        authenticationViewModel.signInWithEmailAndPassword(email, password);

        User userAuthenticated = (User) getValue(authenticationViewModel.userAuthenticated());

        assertThat(userAuthenticated).isNotNull();
        assertThat(userAuthenticated.getEmail()).isEqualTo(email);
    }

    @Test
    public void create_user_with_email_and_password() throws InterruptedException {
        String email = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();

        User user = UserFactory.makeUser();
        user.setEmail(email);

        userRepository.saveUser(user).blockingGet();
        authenticationViewModel.createUserWithEmailAndPassword(email, password);

        User userAuthenticated = (User) getValue(authenticationViewModel.userAuthenticated());

        assertThat(userAuthenticated).isNotNull();
        assertThat(userAuthenticated.getEmail()).isEqualTo(email);
    }

    @Test(expected = RuntimeException.class)
    public void user_authenticated_error_message() {
        String email = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();

        User user = UserFactory.makeUser();
        user.setEmail(email);

        userRepository.saveUser(user).blockingGet();
        authenticationViewModel.signInWithEmailAndPassword(UUID.randomUUID().toString(), password);

        userRepository.signInWithEmailAndPassword(UUID.randomUUID().toString(), password).doOnError(throwable -> {
            assertThat(throwable).isNotNull();
            String userAuthenticatedErrorMessage = (String) getValue(authenticationViewModel.userAuthenticatedErrorMessage());
            assertThat(userAuthenticatedErrorMessage).isNotNull();
        }).blockingGet();
    }
}