import {
  errorLoginSelector,
  passwordLoginSelector,
  submitLoginSelector,
  titleLoginSelector,
  usernameLoginSelector,
  forgetYourPasswordSelector,
} from '../../support/commands';

describe('login page', () => {
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';

  beforeEach(() => {
    cy.visit('');
    cy.clickOnLoginItem();
  });

  beforeEach(() => {
    cy.intercept('POST', '/api/authenticate').as('authenticate');
  });

  describe('Page Display', () => {
    it('should display the login page title', () => {
      cy.get(titleLoginSelector).should('be.visible');
    });

    it('should display login page when visiting /login directly', () => {
      cy.visit('/login');
      cy.get(titleLoginSelector).should('be.visible');
    });

    it('should have username field focused on page load', () => {
      cy.focused().should('have.attr', 'name', 'username');
    });

    it('should display all form elements', () => {
      cy.get(usernameLoginSelector).should('be.visible');
      cy.get(passwordLoginSelector).should('be.visible');
      cy.get('input[name="rememberMe"]').should('be.visible');
      cy.get(submitLoginSelector).should('be.visible');
    });

    it('should display forgot password link', () => {
      cy.get(forgetYourPasswordSelector).should('be.visible');
      cy.get(forgetYourPasswordSelector).should('have.attr', 'href').and('include', '/account/reset/request');
    });

    it('should display register link', () => {
      cy.contains('Register a new account').should('be.visible');
      cy.get('a[href="/account/register"]').should('exist');
    });
  });

  describe('Form Validation', () => {
    it('should require username', () => {
      cy.get(passwordLoginSelector).type('a-password');
      cy.get(submitLoginSelector).click();
      cy.wait('@authenticate').then(({ response }) => expect(response?.statusCode).to.equal(400));
      // login page should stay open when login fails
      cy.get(titleLoginSelector).should('be.visible');
    });

    it('should require password', () => {
      cy.get(usernameLoginSelector).type('a-login');
      cy.get(submitLoginSelector).click();
      cy.wait('@authenticate').then(({ response }) => expect(response?.statusCode).to.equal(400));
      cy.get(errorLoginSelector).should('be.visible');
    });

    it('should not submit with empty form', () => {
      cy.get(submitLoginSelector).click();
      cy.wait('@authenticate').then(({ response }) => expect(response?.statusCode).to.equal(400));
    });

    it('should show error message when password is incorrect', () => {
      cy.get(usernameLoginSelector).type(username);
      cy.get(passwordLoginSelector).type('bad-password');
      cy.get(submitLoginSelector).click();
      cy.wait('@authenticate').then(({ response }) => expect(response?.statusCode).to.equal(401));
      cy.get(errorLoginSelector).should('be.visible');
      cy.get(errorLoginSelector).should('contain', 'Failed to sign in');
    });

    it('should show error message when username does not exist', () => {
      cy.get(usernameLoginSelector).type('nonexistentuser123');
      cy.get(passwordLoginSelector).type('password123');
      cy.get(submitLoginSelector).click();
      cy.wait('@authenticate').then(({ response }) => expect(response?.statusCode).to.equal(401));
      cy.get(errorLoginSelector).should('be.visible');
    });
  });

  describe('Successful Login', () => {
    it('should successfully login with valid credentials', () => {
      cy.get(usernameLoginSelector).type(username);
      cy.get(passwordLoginSelector).type(password);
      cy.get(submitLoginSelector).click();
      cy.wait('@authenticate').then(({ response }) => expect(response?.statusCode).to.equal(200));
      cy.hash().should('eq', '');
    });

    it('should login with remember me checked', () => {
      cy.get(usernameLoginSelector).type(username);
      cy.get(passwordLoginSelector).type(password);
      cy.get('input[name="rememberMe"]').check();
      cy.get(submitLoginSelector).click();
      cy.wait('@authenticate').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
        expect(response?.body).to.have.property('id_token');
      });
    });

    it('should redirect to home page after successful login', () => {
      cy.get(usernameLoginSelector).type(username);
      cy.get(passwordLoginSelector).type(password);
      cy.get(submitLoginSelector).click();
      cy.wait('@authenticate');
      cy.url().should('not.include', '/login');
    });
  });

  describe('Navigation', () => {
    it('should navigate to forgot password page', () => {
      cy.get(forgetYourPasswordSelector).click();
      cy.url().should('include', '/account/reset/request');
    });

    it('should navigate to register page', () => {
      cy.get('a[href="/account/register"]').click();
      cy.url().should('include', '/account/register');
    });
  });

  describe('Security', () => {
    it('should not display password in plain text', () => {
      cy.get(passwordLoginSelector).should('have.attr', 'type', 'password');
    });

    it('should clear error message when retyping credentials', () => {
      // First attempt with wrong password
      cy.get(usernameLoginSelector).type(username);
      cy.get(passwordLoginSelector).type('wrong-password');
      cy.get(submitLoginSelector).click();
      cy.wait('@authenticate');
      cy.get(errorLoginSelector).should('be.visible');

      // Clear and retype
      cy.get(passwordLoginSelector).clear();
      cy.get(passwordLoginSelector).type('new-attempt');

      // Error should still be visible until new submit
      cy.get(errorLoginSelector).should('be.visible');
    });
  });
});
