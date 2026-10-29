import { useEffect, useState } from 'react'
import { api } from '../api/client'
import Alert from '../components/Alert'
import DataTable from '../components/DataTable'
import DetailPanel from '../components/DetailPanel'
import DeleteAllButton from '../components/DeleteAllButton'
import TableActions from '../components/TableActions'

export default function CropFamiliesPage() {
  const [rows, setRows] = useState([])
  const [unused, setUnused] = useState([])
  const [result, setResult] = useState(null)
  const [name, setName] = useState('')
  const [editId, setEditId] = useState(null)
  const [detail, setDetail] = useState(null)

  function load() {
    api('/api/crop-families/all').then((response) => response.status === 200 && setRows(response.body))
    api('/api/crop-families/unused').then((response) => response.status === 200 && setUnused(response.body))
  }

  useEffect(load, [])

  function resetForm() {
    setName('')
    setEditId(null)
  }

  async function submit(event) {
    event.preventDefault()
    const response = editId
      ? await api(`/api/crop-families/update/${editId}`, { method: 'PUT', body: { name } })
      : await api('/api/crop-families/save', { method: 'POST', body: { name } })
    setResult(response)
    if (response.status === 200 || response.status === 201) {
      resetForm()
      load()
    }
  }

  async function view(id) {
    const response = await api(`/api/crop-families/${id}`)
    if (response.status === 200) {
      setDetail([
        ['Name', response.body.name],
        ['Id', response.body.id],
      ])
    } else {
      setResult(response)
    }
  }

  function edit(row) {
    setEditId(row.id)
    setName(row.name)
    setDetail(null)
  }

  async function removeAll() {
    const response = await api('/api/crop-families/delete-all', { method: 'DELETE' })
    setResult(response)
    if (response.status === 200) {
      resetForm()
      load()
    }
  }

  async function remove(row) {
    if (!window.confirm(`Delete crop family "${row.name}"?`)) {
      return
    }
    const response = await api(`/api/crop-families/delete/${row.id}`, { method: 'DELETE' })
    setResult(response)
    if (response.status === 200) {
      load()
      if (editId === row.id) {
        resetForm()
      }
    }
  }

  return (
    <section>
      <h2>Crop families</h2>
      <Alert result={result} onDismiss={() => setResult(null)} />
      <DetailPanel title="Crop family details" fields={detail} onClose={() => setDetail(null)} />
      <form className="form panel" onSubmit={submit}>
        <div className="form-heading">
          <strong>{editId ? 'Update crop family' : 'Create crop family'}</strong>
          {editId && (
            <button type="button" className="small ghost" onClick={resetForm}>
              Cancel edit
            </button>
          )}
        </div>
        <label>
          Name
          <input name="name" value={name} onChange={(event) => setName(event.target.value)} required />
        </label>
        <button type="submit">{editId ? 'Update' : 'Save'}</button>
      </form>
      {unused.length > 0 && (
        <p className="info-banner">Unused families: {unused.map((family) => family.name).join(', ')}</p>
      )}
      <DataTable
        title="All crop families"
        subtitle={`${rows.length} registered`}
        isEmpty={rows.length === 0}
        emptyMessage="No crop families yet."
        headerActions={<DeleteAllButton label="crop families" onDelete={removeAll} disabled={rows.length === 0} />}
      >
        <thead>
          <tr>
            <th>Name</th>
            <th className="col-actions">Actions</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((family) => (
            <tr key={family.id}>
              <td className="cell-strong">{family.name}</td>
              <td className="col-actions">
                <TableActions onView={() => view(family.id)} onEdit={() => edit(family)} onDelete={() => remove(family)} />
              </td>
            </tr>
          ))}
        </tbody>
      </DataTable>
    </section>
  )
}
