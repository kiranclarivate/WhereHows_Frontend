import Ember from 'ember';
import { currentUser } from 'wherehows-web/utils/api/authentication';
import { IUser } from 'wherehows-web/typings/api/authentication/user';

const { get, set, inject: { service }, Service } = Ember;
/**
 * Indicates that the current user has already been tracked in the current session
 * @type {boolean}
 * @private
 */
let _hasUserBeenTracked = false;

export default Service.extend({
  session: service(),

  /**
   * Attempt to load the currently logged in user.
   *   If the userName is found from a previously retained session,
   *   append to service. Request the full user object, and append
   *   to service.
   * @returns {Promise}
   */
  async load(): Promise<void> {
    // If we have a valid session, get the currently logged in user, and set the currentUser attribute,
    // otherwise raise an exception
    if (get(this, 'session.isAuthenticated')) {
      const user: IUser = await currentUser();
      set(this, 'currentUser', user);
    }
  },

  /**
   * Invalidates the current session if the session is currently valid
   * useful if, for example, the server is no able to provide the currently logged in user
   * @return {Boolean|*|Ember.RSVP.Promise}
   */
  invalidateSession() {
    const sessionService = get(this, 'session');
    return sessionService.isAuthenticated && sessionService.invalidate();
  },

  /**
   * Uses the provided tracking function to track the currently logged in user's userId

   * This is not a self-contained method, since it depends on other values that cannot be enforced as dependency
   * of this service as a whole. The dependency on the tracking implementation is also not a concern of this service,
   * injecting as a function is the better approach.
   * @param {Function} userIdTracker a function that takes the userId and tracks it
   */
  trackCurrentUser(userIdTracker: (...args: Array<any>) => void = () => void 0) {
    const userId: string = get(this, 'currentUser.userName');

    // If we have a non-empty userId, the user hasn't already been tracked and the userIdTracker is a valid argument
    // then track the user and toggle the flag affirmative
    if (userId && !_hasUserBeenTracked && typeof userIdTracker === 'function') {
      userIdTracker(userId);
      _hasUserBeenTracked = true;
    }
  }
});
