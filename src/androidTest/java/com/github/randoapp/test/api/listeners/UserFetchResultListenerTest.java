package com.github.randoapp.test.api.listeners;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.filters.SmallTest;

import com.github.randoapp.api.beans.User;
import com.github.randoapp.api.callback.OnFetchUser;
import com.github.randoapp.api.listeners.UserFetchResultListener;
import com.github.randoapp.test.api.APITestHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.InstanceOf;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class UserFetchResultListenerTest {

    @Test
    public void testFetchUserInRandosParsing() throws Exception {
        OnFetchUser onFetchUseMock = spy(new OnFetchUserAssertions());
        new UserFetchResultListener(onFetchUseMock).onResponse(APITestHelper.getUserFetchJSONObject());
        verify(onFetchUseMock, times(1)).onFetch((User) argThat(new InstanceOf(User.class)));
    }

    public class OnFetchUserAssertions implements OnFetchUser {
        @Override
        public void onFetch(User user) {
            //User parsed correctly
            assertThat(user.email, is("user@gmail.com"));

            //RandosIn parsed correctly
            assertThat(user.randosIn.size(), is(1));

            assertThat(user.randosIn.get(0).randoId, is("fd35d7c5086f98c49981280d279e225f6c930a7339"));
            assertThat(user.randosIn.get(0).imageURL, is("http://s3.amazonaws.com/dev.img.l.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg"));
            assertThat(user.randosIn.get(0).imageURLSize.large, is("http://s3.amazonaws.com/dev.img.l.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg"));
            assertThat(user.randosIn.get(0).imageURLSize.medium, is("http://s3.amazonaws.com/dev.img.m.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg"));
            assertThat(user.randosIn.get(0).imageURLSize.small, is("http://s3.amazonaws.com/dev.img.s.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg"));
            assertThat(user.randosIn.get(0).mapURL, is("http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg"));
            assertThat(user.randosIn.get(0).mapURLSize.large, is("http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg"));
            assertThat(user.randosIn.get(0).mapURLSize.medium, is("http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg"));
            assertThat(user.randosIn.get(0).mapURLSize.small, is("http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg"));
            assertThat(user.randosIn.get(0).date.compareTo(new Date(1402309411776l)), is(0));

            //RandosOut parsed correctly
            assertThat(user.randosOut.size(), is(2));

            assertThat(user.randosOut.get(0).randoId, is("bdae3ae04c121f0169f7b011941eb916fc0c43bd56"));
            assertThat(user.randosOut.get(0).imageURL, is("http://s3.amazonaws.com/dev.img.l.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg"));
            assertThat(user.randosOut.get(0).imageURLSize.large, is("http://s3.amazonaws.com/dev.img.l.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg"));
            assertThat(user.randosOut.get(0).imageURLSize.medium, is("http://s3.amazonaws.com/dev.img.m.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg"));
            assertThat(user.randosOut.get(0).imageURLSize.small, is("http://s3.amazonaws.com/dev.img.s.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg"));
            assertThat(user.randosOut.get(0).mapURL, is("http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg"));
            assertThat(user.randosOut.get(0).mapURLSize.large, is("http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg"));
            assertThat(user.randosOut.get(0).mapURLSize.medium, is("http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg"));
            assertThat(user.randosOut.get(0).mapURLSize.small, is("http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg"));
            assertThat(user.randosOut.get(0).date.compareTo(new Date(1402667705513l)), is(0));

            assertThat(user.randosOut.get(1).randoId, is("3721d383e2a339f23f5833e6eb8aeb6eadde65206b"));
            assertThat(user.randosOut.get(1).imageURL, is("http://s3.amazonaws.com/dev.img.l.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg"));
            assertThat(user.randosOut.get(1).imageURLSize.large, is("http://s3.amazonaws.com/dev.img.l.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg"));
            assertThat(user.randosOut.get(1).imageURLSize.medium, is("http://s3.amazonaws.com/dev.img.m.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg"));
            assertThat(user.randosOut.get(1).imageURLSize.small, is("http://s3.amazonaws.com/dev.img.s.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg"));
            assertThat(user.randosOut.get(1).mapURL, is("http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg"));
            assertThat(user.randosOut.get(1).mapURLSize.large, is("http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg"));
            assertThat(user.randosOut.get(1).mapURLSize.medium, is("http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg"));
            assertThat(user.randosOut.get(1).mapURLSize.small, is("http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg"));
            assertThat(user.randosOut.get(1).date.compareTo(new Date(1402667703607l)), is(0));
        }
    }
}
