export default function TableActions({ onView, onEdit = () => {}, onDelete, showEdit = true, disableDelete }) {
  return (
    <div className="table-actions">
      <button type="button" className="small ghost" onClick={onView}>
        View
      </button>
      {showEdit && (
        <button type="button" className="small ghost" onClick={onEdit}>
          Edit
        </button>
      )}
      <button type="button" className="small danger" onClick={onDelete} disabled={disableDelete}>
        Delete
      </button>
    </div>
  )
}
