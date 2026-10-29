export default function CropFamilyPicker({ families, selectedIds, onChange, required }) {
  function addFamily(familyId) {
    if (!familyId || selectedIds.includes(familyId)) {
      return
    }
    onChange([...selectedIds, familyId])
  }

  function removeFamily(familyId) {
    onChange(selectedIds.filter((id) => id !== familyId))
  }

  const available = families.filter((family) => !selectedIds.includes(family.id))

  return (
    <div className="crop-family-picker">
      <select
        value=""
        onChange={(event) => addFamily(event.target.value)}
        required={required && selectedIds.length === 0}
      >
        <option value="">Select crop family</option>
        {available.map((family) => (
          <option key={family.id} value={family.id}>
            {family.name}
          </option>
        ))}
      </select>
      {selectedIds.length > 0 && (
        <div className="crop-family-picker-chips">
          {selectedIds.map((familyId) => {
            const family = families.find((item) => item.id === familyId)
            if (!family) {
              return null
            }
            return (
              <span className="crop-family-chip" key={familyId}>
                {family.name}
                <button type="button" aria-label={`Remove ${family.name}`} onClick={() => removeFamily(familyId)}>
                  ×
                </button>
              </span>
            )
          })}
        </div>
      )}
    </div>
  )
}
