const styles = {
  PLANNED: 'planned',
  GROWING: 'growing',
  HARVESTED: 'harvested',
  FAILED: 'failed',
}

export default function StatusBadge({ status }) {
  const tone = styles[status] || 'neutral'
  return <span className={`status-badge status-${tone}`}>{status}</span>
}
