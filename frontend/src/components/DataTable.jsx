export default function DataTable({ title, subtitle, isEmpty, emptyMessage, headerActions, children }) {
  if (isEmpty) {
    return (
      <section className="table-shell table-shell-empty">
        {(title || headerActions) && (
          <header className="table-header">
            <div>
              {title && <h3 className="table-title">{title}</h3>}
            </div>
            {headerActions}
          </header>
        )}
        <p>{emptyMessage || 'No records to show yet.'}</p>
      </section>
    )
  }

  return (
    <section className="table-shell">
      {(title || subtitle || headerActions) && (
        <header className="table-header">
          <div>
            {title && <h3 className="table-title">{title}</h3>}
            {subtitle && <p className="table-subtitle">{subtitle}</p>}
          </div>
          {headerActions}
        </header>
      )}
      <div className="table-scroll">
        <table className="data-table">{children}</table>
      </div>
    </section>
  )
}
