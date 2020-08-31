package com.google.step.coffee;

import com.google.appengine.api.datastore.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class JsonServletRequest extends HttpServletRequestWrapper {
  /**
   * Constructs a request object wrapping the given request.
   *
   * @param request the {@link HttpServletRequest} to be wrapped.
   * @throws IllegalArgumentException if the request is null
   */
  public JsonServletRequest(HttpServletRequest request) {
    super(request);
  }

  /**
   * Returns the value of a specified request parameter, or throws <code>HttpError</code>
   * if the parameter doesn't exist
   *
   * @param name parameter name
   * @return parameter value
   * @throws HttpError with <code>errorCode = SC_BAD_REQUEST</code> if the parameter doesn't exist
   */
  public String getRequiredParameter(String name) throws HttpError {
    String value = getParameter(name);
    if (value == null) {
      throw new HttpError(HttpServletResponse.SC_BAD_REQUEST, "No value specified for parameter '" + name + "'");
    }
    return value;
  }

  /**
   * Returns <code>Key</code>, whose string encoding is specified in the parameter <code>parameterName</code>
   *
   * @param parameterName parameter name
   * @param kind kind of an <code>Entity</code> the key represents
   * @return decoded key
   * @throws HttpError if key doesn't exist or is of the wrong kind
   */
  public Key getKeyFromParameter(String parameterName, String kind) throws HttpError {
    String encoded = getRequiredParameter(parameterName);

    try {
      Key key = KeyFactory.stringToKey(encoded);
      if (key.getKind().equals(kind)) {
        return key;
      }
    } catch (IllegalArgumentException ignored) {}

    throw new HttpError(HttpServletResponse.SC_BAD_REQUEST, "Invalid value for parameter '" + parameterName + "'");
  }

  /**
   * Returns <code>Entity</code> whose key is encoded in the parameter <code>parameterName</code>
   *
   * @param parameterName parameter name
   * @param kind expected kind of the entity
   * @return the entity object
   * @throws HttpError if key is invalid or the entity doesn't exist
   */
  public Entity getEntityFromParameter(String parameterName, String kind) throws HttpError {
    Key key = getKeyFromParameter(parameterName, kind);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    try {
      return datastore.get(key);
    } catch (EntityNotFoundException exception) {
      throw new HttpError(HttpServletResponse.SC_NOT_FOUND, "Requested " + kind + " doesn't exist");
    }
  }
}
