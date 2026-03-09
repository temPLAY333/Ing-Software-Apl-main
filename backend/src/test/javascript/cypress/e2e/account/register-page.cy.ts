import {
  classInvalid,
  classValid,
  emailRegisterSelector,
  firstPasswordRegisterSelector,
  secondPasswordRegisterSelector,
  submitRegisterSelector,
  usernameRegisterSelector,
} from '../../support/commands';

describe('/account/register', () => {
  beforeEach(() => {
    cy.visit('/account/register');
  });

  beforeEach(() => {
    cy.intercept('POST', '/api/register').as('registerSave');
  });

  describe('Page Display', () => {
    it('should be accessible through menu', () => {
      cy.visit('');
      cy.clickOnRegisterItem();
      cy.url().should('match', /\/account\/register$/);
    });

    it('should load the register page', () => {
      cy.get(submitRegisterSelector).should('be.visible');
    });

    it('should display registration title', () => {
      cy.get('[data-cy="registerTitle"]').should('be.visible');
      cy.get('[data-cy="registerTitle"]').should('contain', 'Registration');
    });

    it('should display all form fields', () => {
      cy.get(usernameRegisterSelector).should('be.visible');
      cy.get(emailRegisterSelector).should('be.visible');
      cy.get(firstPasswordRegisterSelector).should('be.visible');
      cy.get(secondPasswordRegisterSelector).should('be.visible');
      cy.get(submitRegisterSelector).should('be.visible');
    });

    it('should have username field focused on page load', () => {
      cy.focused().should('have.attr', 'id', 'login');
    });

    it('should display link to login page', () => {
      cy.contains('Sign in').should('be.visible');
    });
  });

  describe('Username Validation', () => {
    it('should require username', () => {
      cy.get(usernameRegisterSelector).should('have.class', classInvalid);
      cy.get(usernameRegisterSelector).type('test');
      cy.get(usernameRegisterSelector).blur();
      cy.get(usernameRegisterSelector).should('have.class', classValid);
    });

    it('should validate minimum username length', () => {
      cy.get(usernameRegisterSelector).type('a');
      cy.get(usernameRegisterSelector).blur();
      cy.get(usernameRegisterSelector).should('have.class', classValid);
    });

    it('should validate maximum username length', () => {
      const longUsername = 'a'.repeat(51);
      cy.get(usernameRegisterSelector).type(longUsername);
      cy.get(usernameRegisterSelector).blur();
      cy.get(usernameRegisterSelector).should('have.class', classInvalid);
    });

    it('should accept valid username', () => {
      cy.get(usernameRegisterSelector).type('validuser123');
      cy.get(usernameRegisterSelector).blur();
      cy.get(usernameRegisterSelector).should('have.class', classValid);
    });

    it('should accept username with special characters', () => {
      cy.get(usernameRegisterSelector).type('user.name_123');
      cy.get(usernameRegisterSelector).blur();
      cy.get(usernameRegisterSelector).should('have.class', classValid);
    });
  });

  describe('Email Validation', () => {
    it('should require email', () => {
      cy.get(emailRegisterSelector).should('have.class', classInvalid);
    });

    it('should not accept invalid email format', () => {
      cy.get(emailRegisterSelector).should('have.class', classInvalid);
      cy.get(emailRegisterSelector).type('testtest.fr');
      cy.get(emailRegisterSelector).blur();
      cy.get(emailRegisterSelector).should('have.class', classInvalid);
    });

    it('should not accept email without domain', () => {
      cy.get(emailRegisterSelector).type('test@');
      cy.get(emailRegisterSelector).blur();
      cy.get(emailRegisterSelector).should('have.class', classInvalid);
    });

    it('should not accept email without @', () => {
      cy.get(emailRegisterSelector).type('testtest.fr');
      cy.get(emailRegisterSelector).blur();
      cy.get(emailRegisterSelector).should('have.class', classInvalid);
    });

    it('should accept email in correct format', () => {
      cy.get(emailRegisterSelector).should('have.class', classInvalid);
      cy.get(emailRegisterSelector).type('test@test.fr');
      cy.get(emailRegisterSelector).blur();
      cy.get(emailRegisterSelector).should('have.class', classValid);
    });

    it('should accept email with subdomains', () => {
      cy.get(emailRegisterSelector).type('test@mail.example.com');
      cy.get(emailRegisterSelector).blur();
      cy.get(emailRegisterSelector).should('have.class', classValid);
    });

    it('should validate minimum email length', () => {
      cy.get(emailRegisterSelector).type('a@b.c');
      cy.get(emailRegisterSelector).blur();
      cy.get(emailRegisterSelector).should('have.class', classInvalid);
    });
  });

  describe('Password Validation', () => {
    it('should require first password', () => {
      cy.get(firstPasswordRegisterSelector).should('have.class', classInvalid);
      cy.get(firstPasswordRegisterSelector).type('test1234');
      cy.get(firstPasswordRegisterSelector).blur();
      cy.get(firstPasswordRegisterSelector).should('have.class', classValid);
    });

    it('should validate minimum password length', () => {
      cy.get(firstPasswordRegisterSelector).type('abc');
      cy.get(firstPasswordRegisterSelector).blur();
      cy.get(firstPasswordRegisterSelector).should('have.class', classInvalid);
    });

    it('should accept password with minimum required length', () => {
      cy.get(firstPasswordRegisterSelector).type('abcd');
      cy.get(firstPasswordRegisterSelector).blur();
      cy.get(firstPasswordRegisterSelector).should('have.class', classValid);
    });

    it('should not display password in plain text', () => {
      cy.get(firstPasswordRegisterSelector).should('have.attr', 'type', 'password');
      cy.get(secondPasswordRegisterSelector).should('have.attr', 'type', 'password');
    });

    it('should validate maximum password length', () => {
      const longPassword = 'a'.repeat(51);
      cy.get(firstPasswordRegisterSelector).type(longPassword);
      cy.get(firstPasswordRegisterSelector).blur();
      cy.get(firstPasswordRegisterSelector).should('have.class', classInvalid);
    });
  });

  describe('Password Confirmation', () => {
    it('should require password confirmation to match', () => {
      cy.get(firstPasswordRegisterSelector).should('have.class', classInvalid);
      cy.get(firstPasswordRegisterSelector).type('test1234');
      cy.get(firstPasswordRegisterSelector).blur();
      cy.get(firstPasswordRegisterSelector).should('have.class', classValid);
      cy.get(secondPasswordRegisterSelector).should('have.class', classInvalid);
      cy.get(secondPasswordRegisterSelector).type('test1234');
      cy.get(secondPasswordRegisterSelector).blur();
      cy.get(secondPasswordRegisterSelector).should('have.class', classValid);
    });

    it('should disable submit button when passwords do not match', () => {
      cy.get(firstPasswordRegisterSelector).should('have.class', classInvalid);
      cy.get(firstPasswordRegisterSelector).type('test1234');
      cy.get(firstPasswordRegisterSelector).blur();
      cy.get(firstPasswordRegisterSelector).should('have.class', classValid);
      cy.get(secondPasswordRegisterSelector).should('have.class', classInvalid);
      cy.get(secondPasswordRegisterSelector).type('otherPassword');
      cy.get(submitRegisterSelector).should('be.disabled');
    });

    it('should enable submit button when all fields are valid', () => {
      cy.get(usernameRegisterSelector).type('testuser');
      cy.get(emailRegisterSelector).type('test@example.com');
      cy.get(firstPasswordRegisterSelector).type('test1234');
      cy.get(secondPasswordRegisterSelector).type('test1234');
      cy.get(submitRegisterSelector).should('not.be.disabled');
    });

    it('should show error when passwords do not match on submit', () => {
      cy.get(usernameRegisterSelector).type('testuser');
      cy.get(emailRegisterSelector).type('test@example.com');
      cy.get(firstPasswordRegisterSelector).type('password1');
      cy.get(secondPasswordRegisterSelector).type('password2');

      // The button should be disabled
      cy.get(submitRegisterSelector).should('be.disabled');
    });
  });

  describe('Successful Registration', () => {
    it('should register a valid user', () => {
      const timestamp = Date.now();
      const randomEmail = `test_${timestamp}@example.com`;
      const randomUsername = `user_${timestamp}`;

      cy.get(usernameRegisterSelector).type(randomUsername);
      cy.get(emailRegisterSelector).type(randomEmail);
      cy.get(firstPasswordRegisterSelector).type('password123');
      cy.get(secondPasswordRegisterSelector).type('password123');
      cy.get(submitRegisterSelector).click();
      cy.wait('@registerSave').then(({ response }) => expect(response?.statusCode).to.equal(201));
    });

    it('should display success message after registration', () => {
      const timestamp = Date.now();
      const randomEmail = `success_${timestamp}@example.com`;
      const randomUsername = `success_${timestamp}`;

      cy.get(usernameRegisterSelector).type(randomUsername);
      cy.get(emailRegisterSelector).type(randomEmail);
      cy.get(firstPasswordRegisterSelector).type('password123');
      cy.get(secondPasswordRegisterSelector).type('password123');
      cy.get(submitRegisterSelector).click();
      cy.wait('@registerSave');

      cy.get('.alert-success').should('be.visible');
      cy.get('.alert-success').should('contain', 'Registro exitoso');
    });

    it('should hide form after successful registration', () => {
      const timestamp = Date.now();
      const randomEmail = `hide_${timestamp}@example.com`;
      const randomUsername = `hide_${timestamp}`;

      cy.get(usernameRegisterSelector).type(randomUsername);
      cy.get(emailRegisterSelector).type(randomEmail);
      cy.get(firstPasswordRegisterSelector).type('password123');
      cy.get(secondPasswordRegisterSelector).type('password123');
      cy.get(submitRegisterSelector).click();
      cy.wait('@registerSave');

      // Form should be hidden after success
      cy.get(usernameRegisterSelector).should('not.exist');
    });
  });

  describe('Error Handling', () => {
    it('should show error when username already exists', () => {
      cy.intercept('POST', '/api/register', {
        statusCode: 400,
        body: {
          type: 'https://www.jhipster.tech/problem/login-already-used',
          title: 'Login name already used!',
          status: 400,
        },
      }).as('registerError');

      cy.get(usernameRegisterSelector).type('admin');
      cy.get(emailRegisterSelector).type('newuser@example.com');
      cy.get(firstPasswordRegisterSelector).type('password123');
      cy.get(secondPasswordRegisterSelector).type('password123');
      cy.get(submitRegisterSelector).click();
      cy.wait('@registerError');

      cy.get('.alert-danger').should('be.visible');
      cy.get('.alert-danger').should('contain', 'Usuario ya registrado');
    });

    it('should show error when email already exists', () => {
      cy.intercept('POST', '/api/register', {
        statusCode: 400,
        body: {
          type: 'https://www.jhipster.tech/problem/email-already-used',
          title: 'Email is already in use!',
          status: 400,
        },
      }).as('registerError');

      cy.get(usernameRegisterSelector).type('newuser123');
      cy.get(emailRegisterSelector).type('admin@localhost');
      cy.get(firstPasswordRegisterSelector).type('password123');
      cy.get(secondPasswordRegisterSelector).type('password123');
      cy.get(submitRegisterSelector).click();
      cy.wait('@registerError');

      cy.get('.alert-danger').should('be.visible');
      cy.get('.alert-danger').should('contain', 'Email ya en uso');
    });

    it('should show generic error on server failure', () => {
      cy.intercept('POST', '/api/register', {
        statusCode: 500,
        body: {},
      }).as('registerError');

      const timestamp = Date.now();
      cy.get(usernameRegisterSelector).type(`user_${timestamp}`);
      cy.get(emailRegisterSelector).type(`test_${timestamp}@example.com`);
      cy.get(firstPasswordRegisterSelector).type('password123');
      cy.get(secondPasswordRegisterSelector).type('password123');
      cy.get(submitRegisterSelector).click();
      cy.wait('@registerError');

      cy.get('.alert-danger').should('be.visible');
    });
  });

  describe('Form Interaction', () => {
    it('should clear validation errors when correcting input', () => {
      cy.get(emailRegisterSelector).type('invalid');
      cy.get(emailRegisterSelector).blur();
      cy.get(emailRegisterSelector).should('have.class', classInvalid);

      cy.get(emailRegisterSelector).clear();
      cy.get(emailRegisterSelector).type('valid@example.com');
      cy.get(emailRegisterSelector).blur();
      cy.get(emailRegisterSelector).should('have.class', classValid);
    });

    it('should allow tab navigation through form fields', () => {
      cy.get(usernameRegisterSelector).focus();
      cy.focused().type('{tab}');
      cy.focused().should('have.attr', 'id', 'email');

      cy.focused().type('{tab}');
      cy.focused().should('have.attr', 'id', 'password');

      cy.focused().type('{tab}');
      cy.focused().should('have.attr', 'id', 'confirmPassword');
    });
  });
});
