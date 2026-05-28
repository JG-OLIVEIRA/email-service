CREATE TABLE emails (
    email_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    email_from VARCHAR(50) NOT NULL,
    email_to VARCHAR(50) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    send_date_email TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status_email VARCHAR(20) NOT NULL,
    CONSTRAINT fk_emails_users FOREIGN KEY (user_id) REFERENCES users(user_id)
)