// Generate and persist a unique user ID for this browser
const USER_ID_KEY = 'ats_user_id';

export const getUserId = (): string => {
  let userId = localStorage.getItem(USER_ID_KEY);
  
  if (!userId) {
    // Generate a unique ID (simple UUID v4)
    userId = 'user_' + Date.now() + '_' + Math.random().toString(36).substring(2, 9);
    localStorage.setItem(USER_ID_KEY, userId);
  }
  
  return userId;
};

export const clearUserId = (): void => {
  localStorage.removeItem(USER_ID_KEY);
};
