export function ProductPagination({ page, totalPages, onPage }: { page: number; totalPages: number; onPage: (page: number) => void }) {
  if (totalPages < 2) return null;
  const pages = Array.from({ length: totalPages }, (_, index) => index).filter((value) => value === 0 || value === totalPages - 1 || Math.abs(value - page) <= 1);
  return <nav className="mt-6 flex flex-wrap items-center justify-center gap-2" aria-label="Product pagination">
    <button className="btn btn-sm" disabled={page === 0} onClick={() => onPage(page - 1)}>Previous</button>
    {pages.map((value, index) => <span key={value} className="contents">{index > 0 && value - pages[index - 1] > 1 && <span className="px-1 text-base-content/50">…</span>}<button className={`btn btn-sm ${value === page ? "btn-primary" : "btn-ghost"}`} aria-current={value === page ? "page" : undefined} onClick={() => onPage(value)}>{value + 1}</button></span>)}
    <button className="btn btn-sm" disabled={page === totalPages - 1} onClick={() => onPage(page + 1)}>Next</button>
  </nav>;
}
