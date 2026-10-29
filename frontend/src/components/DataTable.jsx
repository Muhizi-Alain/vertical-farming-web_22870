export default function DataTable({ title, subtitle, isEmpty, emptyMessage, children }) {
  if (isEmpty) {
    return (
      <section className="table-shell table-shell-empty">
        {title && <h3 className="table-title">{title}</h3>}
        <p>{emptyMessage || 'No records to show yet.'}</p>
      </section>
    )
  }

  return (
    <section className="table-shell">
      {(title || subtitle) && (
        <header className="table-header">
          {title && <h3 className="table-title">{title}</h3>}
          {subtitle && <p className="table-subtitle">{subtitle}</p>}
        </header>
      )}
      <div className="table-scroll">
        <table className="data-table">{children}</table>
      </div>
    </section>
  )
}
