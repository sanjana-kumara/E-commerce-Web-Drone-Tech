
# Drone Tech – E-Commerce Web Application

Drone Tech is a Java-based full-stack e-commerce web application for purchasing drone-related products. It features both customer and admin functionalities such as user registration, product browsing, cart and checkout, order history, and admin-side product and user management.

---

## ✅ Database

<img width="1105" height="844" alt="image" src="https://github.com/user-attachments/assets/b882b91c-b85e-4f2f-9089-0a55407bc47f" />

---

## ✅ Main Functional Areas

From the file structure, the system includes:

| Functionality             | Description                                                       |
| ------------------------- | ----------------------------------------------------------------- |
| **User Authentication**   | Sign-up, sign-in, logout, account verification, password recovery |
| **Product Management**    | Admin adds, approves, updates, deletes products                   |
| **Cart & Checkout**       | Users can add to cart, view cart, and place orders                |
| **Order Management**      | Admin can manage and update order statuses                        |
| **User Profile**          | Users can manage their profile information                        |
| **Blog & Info Pages**     | Informational pages like about, blog, FAQ, contact                |
| **Admin User Management** | Admin can manage user status (activate/deactivate)                |

---

## 🔄 Key Business Processes & Flow

### 1. User Registration & Verification Process

**Files involved**: `SignUp.java`, `VerifyAccount.java`, `signup.html`, `account-verify.html`

- User submits the sign-up form.
- `SignUp.java` saves the user info and triggers a verification email.
- `VerifyAccount.java` validates the token and activates the account.

### 2. Login Session Management

**Files**: `SignIn.java`, `SigninCheckFilter.java`, `LogOut.java`, `signin.html`

- `SignIn.java` authenticates the user and starts a session.
- `SigninCheckFilter.java` ensures access control on protected pages.
- `LogOut.java` clears session data and redirects to login.

### 3. Product Management by Admin

**Files**: `SaveProduct.java`, `ApprovedProducts.java`, `ProductApprove.java`, `productadd.html`, `admin-product-management.html`

- Admin adds products using `SaveProduct.java`.
- Pending products are listed.
- `ProductApprove.java` approves/rejects them.
- Approved products become visible in `productview.html`.

### 4. Shopping Cart Process

**Files**: `AddToCart.java`, `LoadCartItems.java`, `RemoveCartItem.java`, `cart.html`

- `AddToCart.java` handles the addition of products to the cart.
- `LoadCartItems.java` retrieves cart data dynamically.
- `RemoveCartItem.java` deletes items from the cart.

### 5. Checkout and Order Placement

**Files**: `Checkout.java`, `GetCheckoutServlet.java`, `VerifyPayments.java`, `checkout.html`

- User proceeds to checkout page.
- Billing info and payment processed by `Checkout.java`.
- `VerifyPayments.java` handles third-party payment validation (via PayHere).

### 6. Order History Tracking

**Files**: `OrderHistory.java`, `orderhistory.html`

- Users can view their past orders.
- Data fetched via `OrderHistory.java`.

### 7. Admin Order Status Management

**Files**: `AdminOrderStatus.java`, `admin-order-management.html`

- Admin views and updates the status of orders (e.g., Shipped, Delivered).

### 8. User Management (Admin)

**Files**: `LoadUsersServlet.java`, `ToggleUserStatus.java`, `admin-user-management.html`

- Lists all users for admin control.
- `ToggleUserStatus.java` activates or deactivates user accounts.

---

## ⚙️ Technologies & Architecture

- **Backend**: Java Servlets + Hibernate ORM
- **Frontend**: HTML, CSS, JS (Vanilla + some modular scripts)
- **Database**: Configured via `hibernate.cfg.xml`
- **Email Service**: Mail.java for verification emails
- **Payment Integration**: PayHere (via `PayHere.java`)

---

## 🙋‍♂️ Author

**M.A. Sanjana Nisal Kumara**  
GitHub: [sanjana-kumara]  
Email: [sanjanakumara92@gmail.com]

---

