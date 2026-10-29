export default function DeleteAllButton({ label, onDelete, disabled }) {
  return (
    <button
      type="button"
      className="small danger"
      disabled={disabled}
      onClick={() => {
        if (window.confirm(`Delete all ${label}? This cannot be undone.`)) {
          onDelete()
        }
      }}
    >
      Delete all
    </button>
  )
}
