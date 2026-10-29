const moves = {
  PLANNED: ['GROWING', 'FAILED'],
  GROWING: ['HARVESTED', 'FAILED'],
}

const workflowClass = {
  GROWING: 'workflow-growing',
  HARVESTED: 'workflow-harvested',
  FAILED: 'workflow-failed',
}

export default function StatusActions({ status, onChange }) {
  const next = moves[status] || []
  if (next.length === 0) {
    return <span className="workflow-final">Final</span>
  }
  return (
    <div className="workflow-actions">
      {next.map((value) => (
        <button
          key={value}
          type="button"
          className={`workflow-pill ${workflowClass[value] || ''}`}
          onClick={() => onChange(value)}
        >
          {value}
        </button>
      ))}
    </div>
  )
}
