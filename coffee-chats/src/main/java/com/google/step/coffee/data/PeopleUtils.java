package com.google.step.coffee.data;

import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.Photo;
import com.google.step.coffee.OAuthService;
import com.google.step.coffee.UserManager;
import com.google.step.coffee.entity.User;

import java.io.IOException;
import java.util.List;

import static com.google.step.coffee.APIUtils.*;

public class PeopleUtils {
  private static PeopleService getPeopleService() {
    String userId = UserManager.getCurrentUserId();

    return new PeopleService.Builder(HTTP_TRANSPORT, JSON_FACTORY, OAuthService.getCredentials(userId))
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  /**
   * Returns current user info, unlike UserManager.getCurrentUser, also fetches
   * user's real name and avatar from Google People API.
   */
  public static User getCurrentUser() {
    User user = UserManager.getCurrentUser();

    if (user == null) {
      return user;
    }

    PeopleService peopleService = getPeopleService();
    Person person;

    try {
      person = peopleService.people().get("people/me")
          .setPersonFields("names,photos").execute();
    } catch (IOException exception) {
      return user;
    }

    User.Builder builder = User.builder()
        .setId(user.id())
        .setEmail(user.email());

    List<Name> names = person.getNames();
    if (!names.isEmpty()) {
      builder.setName(names.get(0).getDisplayName());
    }

    for (Photo photo : person.getPhotos()) {
      if (photo.getDefault()) {
        builder.setAvatarUrl(photo.getUrl());
      }
    }

    return builder.build();
  }
}
