import { useEffect, useState } from 'react'
import { api } from '../api/client'
import Alert, { shouldShowAlert } from '../components/Alert'
import DataTable from '../components/DataTable'
import DetailPanel from '../components/DetailPanel'
import DeleteAllButton from '../components/DeleteAllButton'
import TableActions from '../components/TableActions'

const emptyForm = { cropName: '', variety: '', batchCode: '', supplier: '' }

export default function CropBatchesPage() {
  const [rows, setRows] = useState([])
  const [matches, setMatches] = useState(null)
  const [result, setResult] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [editId, setEditId] = useState(null)
  const [detail, setDetail] = useState(null)

  function load() {
    api('/api/crop-batches/all').then((response) => response.status === 200 && setRows(response.body))
  }

  useEffect(load, [])

  function resetForm() {
    setForm(emptyForm)
    setEditId(null)
  }

  async function submit(event) {
    event.preventDefault()
    const response = editId
      ? await api(`/api/crop-batches/update/${editId}`, { method: 'PUT', body: form })
      : await api('/api/crop-batches/save', { method: 'POST', body: form })
    setResult(response)
    if (response.status === 200 || response.status === 201) {
      resetForm()
      load()
    }
  }

  async function view(id) {
    const response = await api(`/api/crop-batches/${id}`)
    if (response.status === 200) {
      setDetail([
        ['Batch code', response.body.batchCode],
        ['Crop', response.body.cropName],
        ['Variety', response.body.variety],
        ['Supplier', response.body.supplier],
      ])
    } else {
      setResult(response)
    }
  }

  function edit(row) {
    setEditId(row.id)
    setForm({
      cropName: row.cropName,
      variety: row.variety,
      batchCode: row.batchCode,
      supplier: row.supplier,
    })
    setDetail(null)
  }

  async function removeAll() {
    const response = await api('/api/crop-batches/delete-all', { method: 'DELETE' })
    setResult(response)
    if (response.status === 200) {
      resetForm()
      setMatches(null)
      load()
    }
  }

  async function remove(row) {
    if (!window.confirm(`Delete batch ${row.batchCode}?`)) {
      return
    }
    const response = await api(`/api/crop-batches/delete/${row.id}`, { method: 'DELETE' })
    setResult(response)
    if (response.status === 200) {
      load()
      if (editId === row.id) {
        resetForm()
      }
    }
  }

  async function search(event) {
    event.preventDefault()
    const cropName = new FormData(event.currentTarget).get('cropName')
    const response = await api(`/api/crop-batches/by-crop-name?cropName=${encodeURIComponent(cropName)}`)
    if (shouldShowAlert(response)) {
      setResult(response)
    } else {
      setResult(null)
      setMatches(response.body)
    }
  }

  return (
    <section>
      <h2>Crop batches</h2>
      <Alert result={result} onDismiss={() => setResult(null)} />
      <DetailPanel title="Crop batch details" fields={detail} onClose={() => setDetail(null)} />
      <form className="form panel" onSubmit={submit}>
        <div className="form-heading">
          <strong>{editId ? 'Update crop batch' : 'Create crop batch'}</strong>
          {editId && (
            <button type="button" className="small ghost" onClick={resetForm}>
              Cancel edit
            </button>
          )}
        </div>
        <label>
          Crop name
          <input value={form.cropName} onChange={(event) => setForm({ ...form, cropName: event.target.value })} required />
        </label>
        <label>
          Variety
          <input value={form.variety} onChange={(event) => setForm({ ...form, variety: event.target.value })} required />
        </label>
        <label>
          Batch code
          <input value={form.batchCode} onChange={(event) => setForm({ ...form, batchCode: event.target.value })} required />
        </label>
        <label>
          Supplier
          <input value={form.supplier} onChange={(event) => setForm({ ...form, supplier: event.target.value })} required />
        </label>
        <button type="submit">{editId ? 'Update' : 'Save'}</button>
      </form>
      <form className="form panel" onSubmit={search}>
        <label>
          Find by crop name
          <input name="cropName" defaultValue="lettuce" required />
        </label>
        <button type="submit">Search</button>
      </form>
      {matches && (
        <p className="info-banner">
          {matches.length === 0
            ? 'No batches matched that crop name.'
            : `Matches: ${matches.map((batch) => `${batch.variety} (${batch.batchCode})`).join(', ')}`}
        </p>
      )}
      <DataTable
        title="Registered batches"
        subtitle={`${rows.length} batches`}
        isEmpty={rows.length === 0}
        emptyMessage="No crop batches yet."
        headerActions={<DeleteAllButton label="crop batches" onDelete={removeAll} disabled={rows.length === 0} />}
      >
        <thead>
          <tr>
            <th>Batch code</th>
            <th>Crop</th>
            <th>Variety</th>
            <th>Supplier</th>
            <th className="col-actions">Actions</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((batch) => (
            <tr key={batch.id}>
              <td className="cell-strong">{batch.batchCode}</td>
              <td>{batch.cropName}</td>
              <td>{batch.variety}</td>
              <td className="cell-muted">{batch.supplier}</td>
              <td className="col-actions">
                <TableActions onView={() => view(batch.id)} onEdit={() => edit(batch)} onDelete={() => remove(batch)} />
              </td>
            </tr>
          ))}
        </tbody>
      </DataTable>
    </section>
  )
}
