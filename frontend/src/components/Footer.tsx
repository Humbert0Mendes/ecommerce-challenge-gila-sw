const footerLinks = [
  "Work with us",
  "Terms and Conditions",
  "Promotions",
  "Privacy",
  "Accessibility",
  "Contact",
  "Insurance Information",
  "Affiliate Program",
];

export function Footer() {
  return (
    <footer className="footer footer-vertical mt-8 w-full border-t border-base-300 bg-base-200 px-4 py-5 text-base-content sm:footer-horizontal sm:items-center sm:justify-between sm:px-6">
      <nav aria-label="Company information" className="flex max-w-4xl flex-wrap gap-x-4 gap-y-2 text-xs sm:text-sm">
        {footerLinks.map((link) => (
          <a key={link} href="#" className="link link-hover focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary">
            {link}
          </a>
        ))}
      </nav>
      <div className="space-y-1 text-xs text-base-content/70 sm:text-right">
        <p>Copyright © {new Date().getFullYear()} B2B Commerce. All rights reserved.</p>
        <p>Company registration and legal information.</p>
      </div>
    </footer>
  );
}
