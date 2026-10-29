export default function DetailPanel({ title, fields, onClose }) {
  if (!fields) {
    return null
  }

  return (
    <section className="detail-panel">
      <div className="detail-panel-head">
        <h3>{title || 'Record details'}</h3>
        <button type="button" className="small ghost" onClick={onClose}>
          Close
        </button>
      </div>
      <dl className="detail-grid">
        {fields.map(([label, value]) => (
          <div key={label} className="detail-item">
            <dt>{label}</dt>
            <dd>{value ?? '—'}</dd>
          </div>
        ))}
      </dl>
    </section>
  )
}
