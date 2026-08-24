import { useState } from "react";

function Settings() {
  const [darkMode, setDarkMode] = useState(true);
  const [notifications, setNotifications] = useState(true);

  return (
    <div className="settings-page">

      <div className="page-header">

        <div>
          <p className="small-label">
            WORKSPACE PREFERENCES
          </p>

          <h1>Settings</h1>

          <p className="page-description">
            Manage your PrivateDocs workspace preferences.
          </p>
        </div>

        <div className="header-icon">
          ⚙
        </div>

      </div>

      <div className="settings-card">

        <div className="setting-item">

          <div>
            <h3>Dark Mode</h3>

            <p>
              Use the dark interface for your workspace.
            </p>
          </div>

          <button
            className={
              darkMode
                ? "toggle active"
                : "toggle"
            }
            onClick={() =>
              setDarkMode(!darkMode)
            }
          >
            {darkMode ? "ON" : "OFF"}
          </button>

        </div>

        <div className="setting-item">

          <div>
            <h3>Notifications</h3>

            <p>
              Receive workspace notifications.
            </p>
          </div>

          <button
            className={
              notifications
                ? "toggle active"
                : "toggle"
            }
            onClick={() =>
              setNotifications(!notifications)
            }
          >
            {notifications ? "ON" : "OFF"}
          </button>

        </div>

        <div className="setting-item">

          <div>
            <h3>Storage</h3>

            <p>
              Documents are currently stored locally
              in your browser.
            </p>
          </div>

          <span className="storage-status">
            Local
          </span>

        </div>

      </div>

    </div>
  );
}

export default Settings;